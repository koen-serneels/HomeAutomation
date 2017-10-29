package be.error.rpi.heating;

import static be.error.rpi.config.RunConfig.getInstance;
import static be.error.rpi.knx.UdpChannelCommand.HEATING_ENABLED;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.CollectionUtils.extractSingleton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.GroupAddress;

import be.error.rpi.ebus.EbusdTcpCommunicatorImpl;
import be.error.rpi.ebus.commands.SetCurrentRoomTemperature;
import be.error.rpi.ebus.commands.SetDesiredRoomTemperature;
import be.error.rpi.ebus.commands.SetHeatCircruitEnabled;
import be.error.rpi.knx.UdpChannel.UdpChannelCallback;
import be.error.rpi.knx.UdpChannelCommand;
import be.error.types.LocationId;

/**
 * Calculates for each room if heating is required. Heating is required when the delta between desired and actual temperature for that room exceeds a pre defined
 * threshhold. Each room is an active object responsible for gathering the heating parameters and passing it on to the controller using the
 * {@link #send(RoomTemperature)} method. The largest delta between desired and actual temperature (taken from the different rooms) is sent to the heater. Heater
 * communication is done via ebusd. The heater will thus show desired and requested temperature from the room with the largest delta. This is important as the heater
 * will use this information as a parameter (amongst outside temperature) for calculation of the heating curve which controls the water temperature. In other words,
 * the room with the largest delta will play a part in the selection of the heating curve.<p>
 * Besides sending the information to the heater, the controller will also invoke the valves of the registered rooms based on the heating demand
 */
public class HeatingController extends Thread {

	private static final Logger logger = LoggerFactory.getLogger("heating");

	private final EbusDeviceAddress ebusDeviceAddress;
	private final HeatingInfoPollerJobSchedulerFactory heatingInfoPollerJobSchedulerFactory;

	private final BlockingQueue<RoomTemperature> commandQueue = new LinkedBlockingDeque();
	private final ControlValueCalculator controlValueCalculator = new ControlValueCalculator();
	private final List<RoomTemperature> roomTemperatureList = new ArrayList<>();
	private final List<RoomValveController> roomValveControllers = new ArrayList<>();

	private Optional<RoomTemperature> lastSendRoomTemperature = empty();
	private Optional<Boolean> heatingEnabled = empty();
	private Optional<Boolean> lastSendHeatingEnabled = empty();

	private boolean heatingEnabledBecauseOfFrostProtection;

	public HeatingController(final EbusDeviceAddress ebusDeviceAddress, final HeatingInfoPollerJobSchedulerFactory heatingInfoPollerJobSchedulerFactory) {
		this.ebusDeviceAddress = ebusDeviceAddress;
		this.heatingInfoPollerJobSchedulerFactory = heatingInfoPollerJobSchedulerFactory;
	}

	@Override
	public void run() {
		getInstance().addUdpChannelCallback(new UdpChannelCallback() {
			@Override
			public UdpChannelCommand command() {
				return HEATING_ENABLED;
			}

			@Override
			public void callBack(final String enabled) throws Exception {
				logger.debug("Heating enabled status callback received:" + enabled + " [heatingEnabledBecauseOfFrostProtection=" + heatingEnabledBecauseOfFrostProtection
						+ "]");
				if (enabled.equals("1")) {
					logger.debug("Setting heating enabled status to true");
					heatingEnabledBecauseOfFrostProtection = false;
					enableOrDisableHeatingIfNeeded(true);
				} else {
					if (!heatingEnabledBecauseOfFrostProtection) {
						logger.debug("Setting heating enabled status to false");
						enableOrDisableHeatingIfNeeded(false);
					} else {
						logger.debug("Not changing heating enabled status (" + heatingEnabled.get() + ") as heatingEnabledBecauseOfFrostProtection is " + "true");
					}
				}
			}
		});

		while (true) {
			try {
				if (!heatingEnabled.isPresent()) {
					logger.debug("Heating enabled status not yet received. Waiting 20 seconds. Heating controll is offline during this time");
					sleep(20000);
					continue;
				}
				logger.debug("Heating enabled: " + heatingEnabled.get());

				RoomTemperature roomTemperature = commandQueue.take();
				logger.debug("Heating controller received temperature from room " + roomTemperature.getRoomId() + " Details:" + roomTemperature);

				updateList(roomTemperature);

				if (roomTemperatureList.size() != roomValveControllers.size()) {
					logger.debug(
							"Not all temperatures from registered rooms are received. Not controlling heater at this time. Received temperatures:" + roomTemperatureList
									.toString());
					continue;
				}

				control();
				logger.debug("Check if valve update need " + roomTemperature.getRoomId());
				updateValveIfNeeded(roomTemperature);
			} catch (Exception e) {
				logger.error(HeatingController.class.getName() + " had exception while processing. Restarting.", e);
			}
		}
	}

	private void control() throws Exception {
		RoomTemperature sorted = new RoomTemperatureDeltaSorter().sortRoomTemperatureInfos(roomTemperatureList).get(0);
		RoomTemperature previous = lastSendRoomTemperature.orElse(sorted);
		logger.debug("Sorted: " + sorted + " Last send:" + (lastSendRoomTemperature.isPresent() ? lastSendRoomTemperature.get() : null) + " Previous:" + previous);

		process(sorted, previous);

		/**
		 * If heating is disabled, it is expected that the room controllers go into frost protection mode. Ie. they will continue sending the current temperature, but
		 * the desired temperature will be a low temperature (eg. 10°C). If one of the rooms would get colder than this temperature, we enable heating again. From the
		 * moment minimum temperature is reached, heating is turned off again (unless it was re-enabled in the meantime)
		 */
		if (!heatingEnabled.get() || heatingEnabledBecauseOfFrostProtection) {
			RoomTemperature coldest = roomTemperatureList.stream().sorted(comparing(RoomTemperature::getCurrentTemp)).findFirst().get();
			if (coldest.getHeatingDemand()) {
				enableOrDisableHeatingIfNeeded(true);
				heatingEnabledBecauseOfFrostProtection = true;
			} else {
				enableOrDisableHeatingIfNeeded(false);
				heatingEnabledBecauseOfFrostProtection = false;
			}
		}
	}

	private void enableOrDisableHeatingIfNeeded(boolean enabled) throws Exception {
		if (!lastSendHeatingEnabled.isPresent() || lastSendHeatingEnabled.get() != enabled) {
			logger.debug("Setting heating circuit enabled status to " + enabled);
			new EbusdTcpCommunicatorImpl(ebusDeviceAddress).send(new SetHeatCircruitEnabled(enabled));
			lastSendHeatingEnabled = of(enabled);
		} else {
			logger.debug("Not sending heating circuit enabled status. lastSendHeatingEnabled=" + lastSendHeatingEnabled.orElseGet(null) + " value:" + enabled);
		}
		logger.debug("Scheduling job for obtaining heating circuit enabled status");
		heatingInfoPollerJobSchedulerFactory.triggerNow();
		heatingEnabled = of(enabled);
	}

	private void process(RoomTemperature sorted, RoomTemperature previous) throws Exception {
		//Whenever we switch room, the heating controller needs to be 'reset' to reflect the current heating demand of that room
		if (previous.getRoomId() != sorted.getRoomId()) {
			logger.debug("Sending RESET desired temp " + sorted.getDesiredTemp().toString() + " to ebusd for room " + sorted.getRoomId());
			new EbusdTcpCommunicatorImpl(ebusDeviceAddress).send(new SetDesiredRoomTemperature(sorted.getDesiredTemp(), ebusDeviceAddress));

			BigDecimal resetControlTemp = controlValueCalculator.getResetControlValue(sorted.getHeatingDemand(), sorted.getDesiredTemp());
			logger.debug(
					"Sending RESET current temp " + sorted.getCurrentTemp().toString() + " (control temp:" + resetControlTemp.toString() + ") to ebusd for room " + sorted
							.getRoomId());
			new EbusdTcpCommunicatorImpl(ebusDeviceAddress).send(new SetCurrentRoomTemperature(resetControlTemp, sorted.getCurrentTemp()));
		}

		BigDecimal currentTemp = sorted.getCurrentTemp();
		BigDecimal desiredTemp = sorted.getDesiredTemp();

		logger.debug("Sending desired temp " + desiredTemp.toString() + " to ebusd for room " + sorted.getRoomId());
		new EbusdTcpCommunicatorImpl(ebusDeviceAddress).send(new SetDesiredRoomTemperature(desiredTemp, ebusDeviceAddress));

		BigDecimal controlTemp = controlValueCalculator.getControlValue(currentTemp, desiredTemp);
		logger.debug("Sending current temp " + currentTemp.toString() + "(control temp:" + controlTemp.toString() + " to ebusd for room " + sorted.getRoomId());
		new EbusdTcpCommunicatorImpl(ebusDeviceAddress).send(new SetCurrentRoomTemperature(controlTemp, currentTemp));

		lastSendRoomTemperature = of(sorted);

		logger.debug("Scheduling job for obtaining HC status");
		heatingInfoPollerJobSchedulerFactory.triggerNow();
	}

	private void updateValveIfNeeded(RoomTemperature roomTemperature) {
		RoomValveController roomValveController = extractSingleton(
				roomValveControllers.stream().filter(r -> r.getLocationId() == roomTemperature.getRoomId()).collect(toList()));
		roomValveController.updateIfNeeded(roomTemperature.getHeatingDemand());
	}

	public HeatingController registerRoom(LocationId locationId, GroupAddress currentTemperatureGa, GroupAddress... valves) {
		roomValveControllers.add(new RoomValveController(locationId, asList(valves)));
		new RoomTemperatureCollector(locationId, this, currentTemperatureGa).start();
		return this;
	}

	private void updateList(RoomTemperature roomTemperature) {
		Optional<RoomTemperature> optional = this.roomTemperatureList.stream().filter(k -> k.getRoomId() == roomTemperature.getRoomId()).findFirst();

		if (optional.isPresent()) {
			optional.get().update(roomTemperature);
		} else {
			roomTemperatureList.add(roomTemperature);
		}
	}

	void send(RoomTemperature roomTemperature) {
		try {
			commandQueue.put(roomTemperature);
		} catch (Exception e) {
			throw new RuntimeException("Command " + roomTemperature + " not processed", e);
		}
	}
}
