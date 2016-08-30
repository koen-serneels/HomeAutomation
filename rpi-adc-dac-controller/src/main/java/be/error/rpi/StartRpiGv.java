package be.error.rpi;

import static be.error.rpi.config.RunConfig.initialize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.error.rpi.adc.AdcController;
import be.error.rpi.dac.DacController;
import be.error.rpi.dac.dimmer.config.dimmers.DimmerZitHoek;

/**
 * @author Koen Serneels
 */
public class StartRpiGv {

	private static final Logger logger = LoggerFactory.getLogger(StartRpiGv.class);

	private static final String RPI_LAN_IP = "192.168.0.10";

	public static void main(String[] args) throws Exception {
		initialize(RPI_LAN_IP);

		new Thread() {
			@Override
			public void run() {
				try {
					AdcController adcController = new AdcController();
					adcController.run();
				} catch (Exception e) {
					logger.error("AdcController got exception", e);
				}
			}
		}.start();

		new Thread() {
			@Override
			public void run() {
				try {
					DacController dacController = new DacController();
					dacController.run(new DimmerZitHoek());
				} catch (Exception e) {
					logger.error("DacController got exception. Restarting", e);
				}
			}
		}.start();

		logger.debug("Started");
	}
}


