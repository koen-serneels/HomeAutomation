package be.error.rpi.dac.dimmer.config.dimmers.ev;

import static be.error.types.LocationId.BADKAMER;

import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;

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
				outputGroupAddressesSwitchUpdate("4/4/10");

				//inputGroupAddressForOnAndOff("4/4/8");
				inputGroupAddressForOnOffOverride("4/4/8", "4/4/11");
				inputGroupAddressForDimStartAndStop("4/4/9");
				inputGroupAddressForAbsoluteDimValue("14/0/2");
				inputGroupAddressForAbsoluteDimValueOverride("14/0/16");
			}
		}.build();
	}
}
