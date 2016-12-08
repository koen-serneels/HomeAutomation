package be.error.rpi;

import static be.error.rpi.config.RunConfig.initialize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.error.rpi.adc.AdcController;
import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.config.dimmers.DimmerBerging;
import be.error.rpi.dac.dimmer.config.dimmers.DimmerEethoek;
import be.error.rpi.dac.dimmer.config.dimmers.DimmerGang;
import be.error.rpi.dac.dimmer.config.dimmers.DimmerInkomhal;
import be.error.rpi.dac.dimmer.config.dimmers.DimmerKeuken;
import be.error.rpi.dac.dimmer.config.dimmers.DimmerVoordeur;
import be.error.rpi.dac.dimmer.config.dimmers.DimmerWc;
import be.error.rpi.dac.dimmer.config.dimmers.DimmerZitHoek;
import be.error.rpi.dac.dimmer.config.scenes.Gang;
import be.error.rpi.dac.dimmer.config.scenes.GvComfort;

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
				while (true) {
					try {
						AdcController adcController = new AdcController();
						adcController.run();
					} catch (Exception e) {
						logger.error("AdcController got exception. Waiting 10secs before restarting.", e);
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e1) {
							logger.error("AdcController got interrupted while waiting on restart", e1);
							throw new RuntimeException(e1);
						}
					}
				}
			}
		}.start();

		new Thread() {
			@Override
			public void run() {
				try {
					Dimmer dimmerEethoek = new DimmerEethoek().start();
					Dimmer dimmerVoordeur = new DimmerVoordeur().start();
					Dimmer dimmerInkomhal = new DimmerInkomhal().start();
					Dimmer dimmerKeuken = new DimmerKeuken().start();
					Dimmer dimmerZithoek = new DimmerZitHoek().start();
					Dimmer dimmerGang = new DimmerGang().start();
					Dimmer dimmerWc = new DimmerWc().start();
					Dimmer dimmerBerging = new DimmerBerging().start();

					new Gang(dimmerInkomhal, dimmerGang).run();
					new GvComfort(dimmerEethoek, dimmerZithoek, dimmerKeuken).run();
				} catch (Exception e) {
					logger.error("DacController got exception. Restarting", e);
				}
			}
		}.start();

		logger.debug("Started");
	}
}


