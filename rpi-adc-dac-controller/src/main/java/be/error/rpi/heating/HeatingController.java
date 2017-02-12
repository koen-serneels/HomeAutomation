package be.error.rpi.heating;

import static be.error.rpi.config.RunConfig.getInstance;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.commons.lang3.time.DateUtils.addSeconds;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.error.rpi.ebus.EbusdTcpCommunicatorImpl;
import be.error.rpi.ebus.commands.SetCurrentRoomTemperature;
import be.error.rpi.ebus.commands.SetDesiredRoomTemperature;
import be.error.rpi.heating.jobs.HeatingCircuitStatusJob;

/**
 * Calculates for each room if heating is required. Heating is required when the delta between desired and actual temperature for that room exceeds a pre defined
 * threshhold. Each room is an active object responsible for gathering the heating parameters and passing it on to the controller using the
 * {@link #send(RoomTemperature)} method. The largest delta between desired and actual temperature (taken from the different rooms) is sent to the heater. Heater
 * communication is done via ebusd. The heater will thus show desired and requested temperature from the room with the largest delta. This is important as the heater
 * will use this information as a parameter (amongst outside temperature) for calculation of the heating curve which controls the water temperature. In other words,
 * the room with the largest delta will play a part in the selection of the heating curve.<p>
 * <p>
 * Note: after each communication to the heater an asynch job is started to obtain the state of the heating circuit using the {@link HeatingCircuitStatusJob}. The
 * status is send to a specific
 */
public class HeatingController extends Thread {

	private static final Logger logger = LoggerFactory.getLogger("heating");

	private final BlockingQueue<RoomTemperature> commandQueue = new LinkedBlockingDeque();

	private List<RoomTemperature> roomTemperatureList = new ArrayList<>();

	private ControlValueCalculator controlValueCalculator = new ControlValueCalculator();

	private Optional<RoomTemperature> lastSendRoomTemperature = empty();

	@Override
	public void run() {
		while (true) {
			try {
				control();
			} catch (Exception e) {
				logger.error(HeatingController.class.getName() + " had exception while processing. Restarting.", e);
			}
		}
	}

	private void control() throws Exception {
		RoomTemperature roomTemperature = commandQueue.take();
		logger.debug("Heating controller processing temperature from room " + roomTemperature.getRoomId() + " Details:" + roomTemperature);

		updateList(roomTemperature);
		RoomTemperature sorted = new RoomTemperatureDeltaSorter().sortRoomTemperatureInfos(roomTemperatureList).get(0);
		RoomTemperature previous = lastSendRoomTemperature.orElse(sorted);

		//Whenever we switch room, the heating controller needs to be 'reset' to reflect the current heating demand of that room
		if (previous.getRoomId() != sorted.getRoomId()) {
			logger.debug("Sending RESET desired temp " + sorted.getDesiredTemp().toString() + " to ebusd for room " + sorted.getRoomId());
			new EbusdTcpCommunicatorImpl().send(new SetDesiredRoomTemperature(sorted.getDesiredTemp()));

			BigDecimal resetControlTemp = controlValueCalculator.getResetControlValue(sorted.getHeatingDemand(), sorted.getDesiredTemp());
			logger.debug(
					"Sending RESET current temp " + sorted.getCurrentTemp().toString() + " (control temp:" + resetControlTemp.toString() + ") to ebusd for room " +
							sorted
							.getRoomId());
			new EbusdTcpCommunicatorImpl().send(new SetCurrentRoomTemperature(resetControlTemp, sorted.getCurrentTemp()));
		}

		BigDecimal currentTemp = sorted.getCurrentTemp();
		BigDecimal desiredTemp = sorted.getDesiredTemp();

		logger.debug("Sending desired temp " + desiredTemp.toString() + " to ebusd for room " + sorted.getRoomId());
		new EbusdTcpCommunicatorImpl().send(new SetDesiredRoomTemperature(desiredTemp));

		BigDecimal controlTemp = controlValueCalculator.getControlValue(currentTemp, desiredTemp);
		logger.debug("Sending current temp " + currentTemp.toString() + "(control temp:" + controlTemp.toString() + " to ebusd for room " + sorted.getRoomId());
		new EbusdTcpCommunicatorImpl().send(new SetCurrentRoomTemperature(controlTemp, currentTemp));

		lastSendRoomTemperature = of(sorted);

		logger.debug("Scheduling job for obtaining HC status");
		getInstance().getScheduler().scheduleJob(newJob(HeatingCircuitStatusJob.class).build(),
				newTrigger().startAt(addSeconds(new Date(), 30)).withSchedule(simpleSchedule().withRepeatCount(0)).build());
	}

	private void updateList(RoomTemperature roomTemperature) {
		Optional<RoomTemperature> optional = this.roomTemperatureList.stream().filter(k -> k.getRoomId() == roomTemperature.getRoomId()).findFirst();

		if (optional.isPresent()) {
			optional.get().update(roomTemperature);
		} else {
			roomTemperatureList.add(roomTemperature);
		}
	}

	public void send(RoomTemperature roomTemperature) {
		try {
			commandQueue.put(roomTemperature);
		} catch (Exception e) {
			throw new RuntimeException("Command " + roomTemperature + " not processed", e);
		}
	}
}
