package be.error.rpi;

import static be.error.rpi.config.RunConfig.initialize;

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
					dacController.run(new DimmerBadkamer(), new DimmerDressing(), new DimmerNachthal(), new DimmerSk1(), new DimmerAg(), new DimmerLzg(), new DimmerVg
							());
				} catch (Exception e) {
					logger.error("DacController got exception", e);
				}
			}
		}.start();

		logger.debug("Started");
	}
}


