package be.error.rpi.heating;

import static be.error.rpi.config.RunConfig.getInstance;
import static org.apache.commons.lang3.time.DateUtils.addSeconds;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.GroupAddress;

import be.error.rpi.dac.dimmer.config.temperaturecontrol.HeatingCircuitStatusJob;
import be.error.rpi.ebus.EbusdTcpCommunicatorImpl;
import be.error.rpi.ebus.commands.SetCurrentRoomTemperature;
import be.error.rpi.ebus.commands.SetDesiredRoomTemperature;
import be.error.types.LocationId;

/**
 * Calculates for each room if heating is required. Heating is required when the delta between desired and actual temperature for that room exceeds a pre defined
 * threshhold. Each room is an active object responsible for gathering the heating parameters and passing it on to the controller using the
 * {@link #send(RoomTemperature)} method. If heating is required for a room, the corresponding elektro valve(s) are opened. Elekro valves are controlled via KNX.
 * Also, the largest delta between desired and actual temperature (taken from the different rooms) is sent to the heater. Heater communication is done via ebusd. The
 * heater will thus show desired and requested temperature from the room with the largest delta. This is important as the heater will use this information as a
 * parameter (amongst outside temperature) for calculation of the heating curve which controls the water temperature. In other words, the room with the largest delta
 * will play a part in the selection of the heating curve.<p>
 *
 * Resume:
 *
 * <ul>
 * 		<li>Elektro valves are opened for each room in demand of heating. If a room has no heating demand, elektro valves for that room are closed.</li>
 *		<li>The room with the largest delta 'desired-current temperature' is sent to the heater via ebusd which will turn on the given heating circuit</li>
 * </ul>
 */
public class HeatingController extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(HeatingController.class);

	private final BlockingQueue<RoomTemperature> commandQueue = new LinkedBlockingDeque();

	private List<RoomTemperatureInfo> roomTemperatureInfos = new ArrayList<>();

	private ControlValueCalculator controlValueCalculator = new ControlValueCalculator();

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
		getRoomTemperatureInfo(roomTemperature).roomTemperature = roomTemperature;

		if (isInitialized()) {
			List<RoomTemperatureInfo> sorted = new RoomTemperatureDeltaSorter().sortRoomTemperatureInfos(roomTemperatureInfos);

			RoomTemperatureInfo roomTemperatureInfo = sorted.get(0);
			BigDecimal currentTemp = roomTemperatureInfo.roomTemperature.getCurrentTemp();
			BigDecimal desiredTemp = roomTemperatureInfo.roomTemperature.getDesiredTemp();

			logger.debug("Sending desired temp " + desiredTemp.toString() + " to ebusd for room " + roomTemperatureInfo.roomTemperature.getRoomId());
			new EbusdTcpCommunicatorImpl().send(new SetDesiredRoomTemperature(desiredTemp));

			logger.debug("Sending current temp " + currentTemp.toString() + " to ebusd for room " + roomTemperatureInfo.roomTemperature.getRoomId());
			new EbusdTcpCommunicatorImpl().send(new SetCurrentRoomTemperature(controlValueCalculator.getControlValue(currentTemp, desiredTemp), currentTemp));
			logger.debug("Scheduling job for obtaining HC status");
			getInstance().getScheduler().scheduleJob(newJob(HeatingCircuitStatusJob.class).build(),
					newTrigger().startAt(addSeconds(new Date(), 30)).withSchedule(simpleSchedule().withRepeatCount(0)).build());

			logger.debug("Checking to see if valves need to change position");
			roomTemperatureInfos.forEach(r -> {
				r.roomTemperatureProcessor.updateValveStateIfNeeded();
			});
		}
	}

	private RoomTemperatureInfo getRoomTemperatureInfo(RoomTemperature roomTemperature) {
		return roomTemperatureInfos.stream().filter(k -> k.roomTemperatureProcessor.getRoomId() == roomTemperature.getRoomId()).findFirst().get();
	}

	private boolean isInitialized() {
		return roomTemperatureInfos.stream().allMatch(r -> r.roomTemperature != null);
	}

	public void send(RoomTemperature roomTemperature) {
		try {
			commandQueue.put(roomTemperature);
		} catch (Exception e) {
			throw new RuntimeException("Command " + roomTemperature + " not processed", e);
		}
	}

	public void addRoomTemperatureProcessor(final LocationId roomId, GroupAddress currentTemperatureGa, GroupAddress... valves) {
		new RoomTemperatureProcessor(roomId, this, currentTemperatureGa, valves).start();
	}

	public static class RoomTemperatureInfo {

		private RoomTemperature roomTemperature;
		private RoomTemperatureProcessor roomTemperatureProcessor;

		public RoomTemperatureInfo(final RoomTemperatureProcessor roomTemperatureProcessor) {
			this.roomTemperatureProcessor = roomTemperatureProcessor;
		}

		public RoomTemperatureInfo(final RoomTemperature roomTemperature, final RoomTemperatureProcessor roomTemperatureProcessor) {
			this.roomTemperature = roomTemperature;
			this.roomTemperatureProcessor = roomTemperatureProcessor;
		}

		public RoomTemperature getRoomTemperature() {
			return roomTemperature;
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this).append(roomTemperature).toString();
		}
	}
}
