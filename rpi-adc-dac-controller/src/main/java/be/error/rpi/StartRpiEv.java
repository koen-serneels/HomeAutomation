package be.error.rpi;

import static be.error.rpi.config.RunConfig.getInstance;
import static be.error.rpi.config.RunConfig.initialize;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.CronExpression;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.error.rpi.dac.DacController;
import be.error.rpi.dac.dimmer.config.dimmers.buiten.DimmerAg;
import be.error.rpi.dac.dimmer.config.dimmers.buiten.DimmerLzg;
import be.error.rpi.dac.dimmer.config.dimmers.buiten.DimmerVg;
import be.error.rpi.dac.dimmer.config.dimmers.ev.DimmerBadkamer;
import be.error.rpi.dac.dimmer.config.dimmers.ev.DimmerDressing;
import be.error.rpi.dac.dimmer.config.dimmers.ev.DimmerNachthal;
import be.error.rpi.dac.dimmer.config.dimmers.ev.DimmerSk1;
import be.error.rpi.dac.dimmer.config.temperaturecontrol.Ev;
import be.error.rpi.dac.dimmer.config.temperaturecontrol.HeatingCircuitStatusJob;
import be.error.rpi.dac.dimmer.config.ventilation.VentilationUdpCallback;

/**
 * @author Koen Serneels
 */
public class StartRpiEv {

	private static final Logger logger = LoggerFactory.getLogger(StartRpiEv.class);

	private static final String RPI_LAN_IP = "192.168.0.11";

	public static void main(String[] args) throws Exception {
		initialize(RPI_LAN_IP);

		new Thread() {
			@Override
			public void run() {
				try {
					DacController dacController = new DacController();
					Ev ev = new Ev();
					dacController.run(new DimmerBadkamer(), new DimmerDressing(), new DimmerNachthal(), new DimmerSk1(), new DimmerAg(), new DimmerLzg(), new DimmerVg
							());

					getInstance().addUdpChannelCallback(new VentilationUdpCallback());
				} catch (Exception e) {
					logger.error("DacController got exception", e);
				}
			}
		}.start();

		new Thread() {
			@Override
			public void run() {
				try {
					Scheduler scheduler = getInstance().getScheduler();
					scheduler.scheduleJob(newJob(HeatingCircuitStatusJob.class).withIdentity("HeatingCircuitStatusJob.class.getName()").build(),
							newTrigger().withIdentity("HeatingCircuitStatusJob").withSchedule(cronSchedule(new CronExpression("0 0/5 * * * ?"))).startNow().build());
					scheduler.start();
				} catch (Exception e) {
					logger.error("Scheduler got exception. Restarting", e);
				}
			}
		}.start();

		logger.debug("Started");
	}
}


