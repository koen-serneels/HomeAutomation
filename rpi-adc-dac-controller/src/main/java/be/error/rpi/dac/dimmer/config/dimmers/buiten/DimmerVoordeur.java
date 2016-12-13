package be.error.rpi.dac.dimmer.config.dimmers.buiten;

import static be.error.rpi.dac.dimmer.config.DimmerName.VOORDEUR;

import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;

/**
 * Created by koen on 01.10.16.
 */
public class DimmerVoordeur implements DimmerConfig {

	@Override
	public Dimmer start() throws Exception {
		return new DimmerBuilder() {
			{
				name(VOORDEUR);
				ic2BoardAddress(0x5B);
				boardChannel(1);
				delayBeforeIncreasingDimValue(15);

				outputGroupAddressesForVisualisationStatusFeedback("15/0/8");
				outputGroupAddressesForActorSwitchingOnAndOff("1/1/1");
				inputGroupAddressForAbsoluteDimValue("14/0/8");
			}
		}.build();
	}
}