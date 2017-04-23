package be.error.rpi.heating.jobs;

import static be.error.rpi.config.RunConfig.getInstance;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.GroupAddress;

import be.error.rpi.ebus.EbusCommand;
import be.error.rpi.ebus.EbusdTcpCommunicatorImpl;

public class HeatingInfoPollerJob implements Job {

	public static String JOB_CONFIG_KEY = "jobconfig";

	private static final Logger logger = LoggerFactory.getLogger("heating");

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		try {
			logger.debug("Status poller job starting...");
			JobConfig jobConfig = (JobConfig) context.getJobDetail().getJobDataMap().get(JOB_CONFIG_KEY);

			for (Entry<EbusCommand<?>, GroupAddress> entry : jobConfig.getEbusCommands().entrySet()) {
				logger.debug("Requesting status with command " + entry.getKey().getEbusCommands());
				List<String> result = new EbusdTcpCommunicatorImpl(jobConfig.getEbusDeviceAddress()).send(entry.getKey());
				logger.debug("Result for command " + entry.getKey().getEbusCommands() + " -> " + ArrayUtils.toString(result, ""));
				Object converted = entry.getKey().convertResult(result);
				logger.debug("Sending result " + converted + " to GA: " + entry.getValue());

				if (converted instanceof Float) {
					getInstance().getKnxConnectionFactory().createProcessCommunicator().write(entry.getValue(), (Float) converted, false);
				} else if (converted instanceof BigDecimal) {
					getInstance().getKnxConnectionFactory().createProcessCommunicator().write(entry.getValue(), ((BigDecimal) converted).floatValue(), false);
				} else if (converted instanceof Boolean) {
					getInstance().getKnxConnectionFactory().createProcessCommunicator().write(entry.getValue(), (Boolean) converted);
				} else if (converted instanceof String) {
					getInstance().getKnxConnectionFactory().createProcessCommunicator().write(entry.getValue(), (String) converted);
				} else {
					throw new IllegalStateException("Could not send type " + converted.getClass());
				}
			}
		} catch (Exception e) {
			logger.error(HeatingInfoPollerJob.class.getSimpleName() + " exception", e);
			throw new JobExecutionException(e);
		}
	}
}