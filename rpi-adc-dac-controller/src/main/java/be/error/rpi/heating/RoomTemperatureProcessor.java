package be.error.rpi.heating;

import static be.error.rpi.config.RunConfig.getInstance;
import static be.error.rpi.knx.UdpChannelCommand.Constants.TEMPERATURE;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.DetachEvent;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessEvent;

import be.error.rpi.dac.dimmer.builder.AbstractDimmerProcessListener;
import be.error.rpi.knx.UdpChannel.UdpChannelCallback;
import be.error.rpi.knx.UdpChannelCommand;
import be.error.types.LocationId;

public class RoomTemperatureProcessor {

	private static final Logger logger = LoggerFactory.getLogger(RoomTemperatureProcessor.class);

	private final LocationId roomId;
	private final HeatingController heatingController;

	private GroupAddress currentTemperatureGa;
	private Collection<GroupAddress> valves = new ArrayList<>();
	private UdpChannelCommand desiredTempCommand;

	private Optional<Boolean> lastSendValveState = empty();
	private Optional<RoomTemperature> lastSendRoomTemperature = empty();
	private RoomTemperature roomTemperature;

	private ControlValueCalculator controlValueCalculator = new ControlValueCalculator();

	public RoomTemperatureProcessor(final LocationId roomId, final HeatingController heatingController, GroupAddress currentTemperatureGa, GroupAddress... valves) {
		this.roomId = roomId;
		this.heatingController = heatingController;
		this.currentTemperatureGa = currentTemperatureGa;
		this.desiredTempCommand = UdpChannelCommand.fromString(TEMPERATURE + "_" + roomId.toString());
		this.valves = asList(valves);

		roomTemperature = new RoomTemperature(roomId);
	}

	public void start() {
		getInstance().addUdpChannelCallback(new UdpChannelCallback() {
			@Override
			public UdpChannelCommand command() {
				return desiredTempCommand;
			}

			@Override
			public boolean isApplicable(final UdpChannelCommand udpChannelCommand) {
				return udpChannelCommand == desiredTempCommand;
			}

			@Override
			public void callBack(final String desiredTemp) throws Exception {
				logger.debug("Receiving desired temperature " + desiredTemp + "for room " + roomId);
				synchronized (roomTemperature) {
					roomTemperature.updateDesiredTemp(desiredTemp);
					sendRoomTemperatureUpdateIfNeeded();
				}
			}
		});

		getInstance().getKnxConnectionFactory().createProcessCommunicator(new AbstractDimmerProcessListener() {
			@Override
			public void groupWrite(final ProcessEvent e) {
				if (e.getDestination().equals(currentTemperatureGa)) {
					try {
						Double currentTemp = super.asFloat(e, false);
						logger.debug("Receiving current temperature " + currentTemp + "for room " + roomId);
						synchronized (roomTemperature) {
							roomTemperature.updateCurrentTemp(currentTemp);
							sendRoomTemperatureUpdateIfNeeded();
						}
					} catch (Exception exception) {
						logger.error("Error converting received desired temperature value from KNX", exception);
					}
				}
			}

			@Override
			public void groupReadRequest(final ProcessEvent e) {

			}

			@Override
			public void groupReadResponse(final ProcessEvent e) {

			}

			@Override
			public void detached(final DetachEvent e) {

			}
		});
	}

	public void updateValveStateIfNeeded() {
		synchronized (roomTemperature) {
			Optional<Boolean> heatingDemand = controlValueCalculator.hasHeatingDemand(roomTemperature);
			if (heatingDemand.isPresent()) {
				roomTemperature.updateHeatingDemand(heatingDemand.get());
			}

			if (!lastSendValveState.isPresent() || lastSendValveState.get() != roomTemperature.isHeatingDemand()) {
				try {
					ProcessCommunicator pc = getInstance().getKnxConnectionFactory().createProcessCommunicator();
					for (GroupAddress ga : valves) {
						logger.debug("Setting valve " + ga.toString() + " of room " + roomId + " to:" + roomTemperature.isHeatingDemand());
						pc.write(ga, roomTemperature.isHeatingDemand());
					}
					lastSendValveState = of(roomTemperature.isHeatingDemand());
				} catch (Exception e) {
					logger.error("Could not operate valve via KNX", e);
					throw new RuntimeException(e);
				}
			}
		}
	}

	private void sendRoomTemperatureUpdateIfNeeded() {
		logger.debug("Last send room temp info:" + (lastSendRoomTemperature.isPresent() ? lastSendRoomTemperature.get() : "<none>") + " new room temp info:" +
				roomTemperature);

		if (roomTemperature.ready() && (!lastSendRoomTemperature.isPresent() || !lastSendRoomTemperature.get().equals(roomTemperature))) {
			RoomTemperature toSend = roomTemperature.clone();
			logger.debug("Sending room temp info:" + toSend);
			heatingController.send(toSend);
			lastSendRoomTemperature = of(toSend);
		} else {
			logger.debug("Not sending room temp info");
		}
	}

	public LocationId getRoomId() {
		return roomId;
	}
}