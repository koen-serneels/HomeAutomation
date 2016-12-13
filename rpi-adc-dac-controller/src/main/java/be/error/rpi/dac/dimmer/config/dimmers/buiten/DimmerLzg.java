package be.error.rpi.dac.dimmer.config.dimmers.buiten;

import static be.error.rpi.dac.dimmer.config.DimmerName.LZG;
import static be.error.rpi.dac.dimmer.config.DimmerName.VOORDEUR;

import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;

public class DimmerLzg implements DimmerConfig {

	@Override
	public Dimmer start() throws Exception {
		return new DimmerBuilder() {
			{
				name(LZG);
				ic2BoardAddress(0x5A);
				boardChannel(1);
				delayBeforeIncreasingDimValue(15);

				//outputGroupAddressesForVisualisationStatusFeedback("");
				//outputGroupAddressesForActorSwitchingOnAndOff("");
				inputGroupAddressForAbsoluteDimValue("14/0/14");
			}
		}.build();
	}
}