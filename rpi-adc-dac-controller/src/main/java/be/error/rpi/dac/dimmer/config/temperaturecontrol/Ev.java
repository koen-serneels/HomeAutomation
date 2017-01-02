package be.error.rpi.dac.dimmer.config.temperaturecontrol;

import static be.error.rpi.config.RunConfig.getInstance;
import static be.error.rpi.knx.Support.createGroupAddress;
import static be.error.rpi.knx.UdpChannelCommand.TEMPERATURE_EV;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
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

	private static final Logger logger = LoggerFactory.getLogger("ebusd");
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
				logger.debug("Setting desired temp EV to " + s);
				new EbusdTcpCommunicator().send(new SetDesiredRoomTemperature(s));
			}
		});

		getInstance().getKnxConnectionFactory().createProcessCommunicator(new AbstractDimmerProcessListener() {
			@Override
			public void groupWrite(final ProcessEvent e) {
				if (e.getDestination().equals(badkamerTemp)) {
					try {
						Double temp = super.asFloat(e, false);
						logger.debug("Setting current temp EV to " + temp);
						new EbusdTcpCommunicator().send(new SetCurrentRoomTemperature("" + temp));
						getInstance().getScheduler().scheduleJob(newJob(HeatingCircuitStatusJob.class).build(),
								newTrigger().startAt(DateUtils.addSeconds(new Date(), 30)).withSchedule(simpleSchedule().withRepeatCount(0)).build());
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
