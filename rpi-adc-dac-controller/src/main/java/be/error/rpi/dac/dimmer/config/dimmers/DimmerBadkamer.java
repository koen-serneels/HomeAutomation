package be.error.rpi.dac.dimmer.config.dimmers;

import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;
import be.error.rpi.dac.dimmer.config.DimmerName;

/**
 * @author Koen Serneels
 */
public class DimmerBadkamer implements DimmerConfig {

	@Override
	public void start() throws Exception {
		new DimmerBuilder() {
			{
				name(DimmerName.BADKAMER);
				ic2BoardAddress(0x5B);
				boardChannel(3);
				outputGroupAddressesForVisualisationStatusFeedback("15/0/2");
				outputGroupAddressesForActorSwitchingOnAndOff("4/4/1");
				inputGroupAddressForAbsoluteDimValue("14/0/2");
			}
		}.build();
	}
}
