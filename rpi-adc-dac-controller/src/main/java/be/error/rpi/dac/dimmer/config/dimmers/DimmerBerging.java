package be.error.rpi.dac.dimmer.config.dimmers;

import static be.error.rpi.dac.dimmer.config.DimmerName.BERGING;

import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;

/**
 * Created by koen on 03.11.16.
 */
public class DimmerBerging implements DimmerConfig {

	@Override
	public Dimmer start() throws Exception {
		return new DimmerBuilder() {
			{
				name(BERGING);
				ic2BoardAddress(0x59);
				boardChannel(1);
				delayBeforeIncreasingDimValue(15);

				outputGroupAddressesForVisualisationStatusFeedback("15/0/11");
				outputGroupAddressesForActorSwitchingOnAndOff("5/6/1");
				inputGroupAddressForOnAndOff("5/6/0");
				inputGroupAddressForAbsoluteDimValue("14/0/11");
			}
		}.build();
	}
}