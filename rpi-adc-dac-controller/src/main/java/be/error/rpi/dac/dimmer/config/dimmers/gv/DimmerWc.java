package be.error.rpi.dac.dimmer.config.dimmers.gv;

import static be.error.types.LocationId.WC;

import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;

public class DimmerWc implements DimmerConfig {

	@Override
	public Dimmer start() throws Exception {
		return new DimmerBuilder() {
			{
				name(WC);
				ic2BoardAddress(0x59);
				boardChannel(2);
				inputGroupAddressForOnAndOff("5/2/0");
				outputGroupAddressesForVisualisationStatusFeedback("15/0/10");
				outputGroupAddressesForActorSwitchingOnAndOff("5/2/1");
				inputGroupAddressForAbsoluteDimValue("14/0/10");
			}
		}.build();
	}
}
