package be.error.rpi.dac.dimmer.config.dimmers;

import static be.error.rpi.dac.dimmer.config.DimmerName.KEUKEN;

import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;

/**
 * Created by koen on 01.10.16.
 */
public class DimmerKeuken implements DimmerConfig {

	@Override
	public Dimmer start() throws Exception {
		return new DimmerBuilder() {
			{
				name(KEUKEN);
				ic2BoardAddress(0x5B);
				boardChannel(2);
				delayBeforeIncreasingDimValue(15);

				outputGroupAddressesForActorSwitchingOnAndOff("5/0/6", "5/0/7");
				outputGroupAddressesForVisualisationStatusFeedback("15/0/5");
				outputGroupAddressesForSwitchLedControl("5/0/3");
				outputSwitchUpdateGroupAddresses("5/0/5");
				inputGroupAddressForOnAndOff("5/0/5");
				inputGroupAddressForDimStartAndStop("5/0/4");
				inputGroupAddressForAbsoluteDimValue("14/0/5");
			}
		}.build();
	}
}