package be.error.rpi.dac.dimmer.config.dimmers;

import static be.error.rpi.dac.dimmer.config.DimmerName.SK1;

import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;

/**
 * @author Koen Serneels
 */
public class DimmerSk1 implements DimmerConfig {

	@Override
	public Dimmer start() throws Exception {
		return new DimmerBuilder() {
			{
				name(SK1);
				ic2BoardAddress(0x5B);
				boardChannel(0);

				outputGroupAddressesForActorSwitchingOnAndOff("4/0/0", "4/0/9");
				outputGroupAddressesForVisualisationStatusFeedback("15/0/1");
				inputGroupAddressForOnAndOff("4/0/7");
				inputGroupAddressForDimStartAndStop("4/0/8");
				inputGroupAddressForAbsoluteDimValue("14/0/1");
			}
		}.build();
	}
}
