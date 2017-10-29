package be.error.rpi.dac.dimmer.config.dimmers.buiten;

import static be.error.types.LocationId.VG;

import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;

public class DimmerVg implements DimmerConfig {

	@Override
	public Dimmer start() throws Exception {
		return new DimmerBuilder() {
			{
				name(VG);
				ic2BoardAddress(0x5A);
				boardChannel(2);
				delayBeforeIncreasingDimValue(0);

				outputGroupAddressesForActorSwitchingOnAndOff("1/0/0");
				//outputGroupAddressesForVisualisationStatusFeedback("");

				inputGroupAddressForAbsoluteDimValue("14/0/15");
			}
		}.build();
	}
}