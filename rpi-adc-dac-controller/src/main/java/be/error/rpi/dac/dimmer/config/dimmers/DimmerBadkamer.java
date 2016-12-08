package be.error.rpi.dac.dimmer.config.dimmers;

import static be.error.rpi.dac.dimmer.config.DimmerName.BADKAMER;

import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;
import be.error.rpi.dac.dimmer.config.DimmerName;

/**
 * @author Koen Serneels
 */
public class DimmerBadkamer implements DimmerConfig {

	@Override
	public Dimmer start() throws Exception {
		return new DimmerBuilder() {
			{
				name(BADKAMER);
				ic2BoardAddress(0x5B);
				boardChannel(3);
				outputGroupAddressesForVisualisationStatusFeedback("15/0/2");
				outputGroupAddressesForActorSwitchingOnAndOff("4/4/1");

				outputGroupAddressesForSwitchLedControl("4/4/7");
				outputSwitchUpdateGroupAddresses("4/4/10");
				inputGroupAddressForOnAndOff("4/4/8");
				inputGroupAddressForDimStartAndStop("4/4/9");

				inputGroupAddressForAbsoluteDimValue("14/0/2");
			}
		}.build();
	}
}
