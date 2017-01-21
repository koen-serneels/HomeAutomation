package be.error.rpi.heating.jobs;

import static be.error.rpi.config.RunConfig.getInstance;

import java.math.BigDecimal;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.GroupAddress;

import be.error.rpi.ebus.EbusdTcpCommunicatorImpl;
import be.error.rpi.ebus.commands.GetOutsideTemperature;
import be.error.rpi.knx.Support;

public class OutsideTemperatureJob implements Job {

	private static final Logger logger = LoggerFactory.getLogger(OutsideTemperatureJob.class);

	private GroupAddress outsideTempGa = Support.createGroupAddress("10/0/16");

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		try {
			logger.debug("Requesting outside temp");
			GetOutsideTemperature getOutsideTemperature = new GetOutsideTemperature();
			BigDecimal result = getOutsideTemperature.convertResult(new EbusdTcpCommunicatorImpl().send(getOutsideTemperature));
			logger.debug("Communicating outside temp " + result + "°C to " + outsideTempGa);
			getInstance().getKnxConnectionFactory().createProcessCommunicator().write(outsideTempGa, result.floatValue(), false);
		} catch (Exception e) {
			logger.error(OutsideTemperatureJob.class.getSimpleName() + " exception", e);
			throw new JobExecutionException(e);
		}
	}
}