package be.error.rpi.dac.dimmer.config.dimmers.gv;

import static be.error.rpi.dac.dimmer.config.DimmerName.GANG;

import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;

/**
 * Created by koen on 01.10.16.
 */
public class DimmerGang implements DimmerConfig {

	@Override
	public Dimmer start() throws Exception {
		return new DimmerBuilder() {
			{
				name(GANG);
				ic2BoardAddress(0x59);
				boardChannel(3);
				delayBeforeIncreasingDimValue(15);

				outputGroupAddressesForVisualisationStatusFeedback("15/0/9");
				outputGroupAddressesForActorSwitchingOnAndOff("5/4/0");
				inputGroupAddressForAbsoluteDimValue("14/0/9");
			}
		}.build();
	}
}