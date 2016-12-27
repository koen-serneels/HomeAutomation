package be.error.rpi.dac.dimmer.config.thermostat;

import static be.error.rpi.config.RunConfig.getInstance;
import static be.error.rpi.knx.UdpChannelCommand.TEMPERATURE_EV;
import static be.error.rpi.support.Support.createGroupAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.DetachEvent;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.process.ProcessEvent;

import be.error.rpi.dac.dimmer.builder.AbstractDimmerProcessListener;
import be.error.rpi.ebus.EbusdTcpCommunicator;
import be.error.rpi.ebus.commands.SetCurrentRoomTemperature;
import be.error.rpi.ebus.commands.SetDesiredRoomTemperature;
import be.error.rpi.knx.UdpChannel.UdpChannelCallback;
import be.error.rpi.knx.UdpChannelCommand;

public class Ev {

	private static final Logger logger = LoggerFactory.getLogger("temp");
	private final GroupAddress badkamerTemp = createGroupAddress("10/0/4");

	public Ev() throws Exception {
		getInstance().addUdpChannelCallback(new UdpChannelCallback() {
			@Override
			public UdpChannelCommand command() {
				return TEMPERATURE_EV;
			}

			@Override
			public boolean isApplicable(final UdpChannelCommand udpChannelCommand) {
				return udpChannelCommand == TEMPERATURE_EV;
			}

			@Override
			public void callBack(final String s) throws Exception {
				logger.debug("Setting desired temp to " + s);
				new EbusdTcpCommunicator().send(new SetDesiredRoomTemperature(s));
			}
		});

		getInstance().getKnxConnectionFactory().createProcessCommunicator(new AbstractDimmerProcessListener() {
			@Override
			public void groupWrite(final ProcessEvent e) {
				if (e.getDestination().equals(badkamerTemp)) {
					try {
						Double temp = super.asFloat(e, false);
						logger.debug("Setting current temp to " + temp);
						new EbusdTcpCommunicator().send(new SetCurrentRoomTemperature("" + temp));
					} catch (Exception exception) {
						logger.error("Error converted received temperature value from KNX", exception);
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
}
