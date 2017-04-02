package be.error.rpi.heating.jobs;

import static be.error.rpi.config.RunConfig.getInstance;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.GroupAddress;

import be.error.rpi.ebus.EbusdTcpCommunicatorImpl;
import be.error.rpi.ebus.commands.GetHeatingCircuitEnabled;
import be.error.rpi.knx.Support;

public class HeatingCircuitEnabledJob implements Job {

	private static final Logger logger = LoggerFactory.getLogger(HeatingCircuitEnabledJob.class);

	private GroupAddress heatingCircuitEnabledStatusGa = Support.createGroupAddress("10/3/1");

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		try {
			logger.debug("Requesting heating circuit enabled status");
			GetHeatingCircuitEnabled getHeatingCircuitEnabled = new GetHeatingCircuitEnabled();
			boolean result = getHeatingCircuitEnabled.convertResult(new EbusdTcpCommunicatorImpl().send(getHeatingCircuitEnabled));
			logger.debug("Communicating requesting heating circuit enabled status " + result + " " + heatingCircuitEnabledStatusGa);
			getInstance().getKnxConnectionFactory().createProcessCommunicator().write(heatingCircuitEnabledStatusGa, result);
		} catch (Exception e) {
			logger.error(HeatingCircuitEnabledJob.class.getSimpleName() + " exception", e);
			throw new JobExecutionException(e);
		}
	}
}