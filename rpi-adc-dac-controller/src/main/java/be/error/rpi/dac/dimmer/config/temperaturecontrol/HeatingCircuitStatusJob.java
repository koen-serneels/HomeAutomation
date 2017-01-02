package be.error.rpi.dac.dimmer.config.temperaturecontrol;

import static be.error.rpi.config.RunConfig.getInstance;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.GroupAddress;

import be.error.rpi.ebus.EbusdTcpCommunicator;
import be.error.rpi.ebus.commands.HeatingCircuitStatus;
import be.error.rpi.knx.Support;

public class HeatingCircuitStatusJob implements Job {

	private static final Logger logger = LoggerFactory.getLogger(OutsideTemperatureJob.class);

	private GroupAddress heatingCircuitStatusGa = Support.createGroupAddress("10/3/0");

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		try {
			logger.debug("Requesting heating circuit status");
			HeatingCircuitStatus heatingCircuitStatus = new HeatingCircuitStatus();
			boolean result = heatingCircuitStatus.convertResult(new EbusdTcpCommunicator().send(heatingCircuitStatus));
			logger.debug("Communicating requesting heating circuit status " + result + " " + heatingCircuitStatusGa);
			getInstance().getKnxConnectionFactory().createProcessCommunicator().write(heatingCircuitStatusGa, result);
		} catch (Exception e) {
			logger.error(OutsideTemperatureJob.class.getSimpleName() + " exception", e);
			throw new JobExecutionException(e);
		}
	}
}