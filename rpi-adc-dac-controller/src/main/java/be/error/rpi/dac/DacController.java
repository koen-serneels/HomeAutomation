package be.error.rpi.dac;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.error.rpi.dac.dimmer.config.DimmerConfig;

/**
 * @author Koen Serneels
 */
public class DacController {

	private static final Logger logger = LoggerFactory.getLogger(DacController.class);

	public void run(DimmerConfig... dimmerConfigs) throws Exception {
		logger.debug("DAC controller starting");

		for (DimmerConfig dimmerConfig : dimmerConfigs) {
			dimmerConfig.start();
		}
	}
}

