package be.error.rpi.dac.dimmer.config.dimmers;

import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;
import be.error.rpi.dac.dimmer.config.DimmerName;

/**
 * @author Koen Serneels
 */
public class DimmerZitHoek implements DimmerConfig {

	@Override
	public void start() throws Exception {
		new DimmerBuilder() {
			{
				name(DimmerName.ZITHOEK);
				ic2BoardAddress(0x58);
				boardChannel(3);
				delayBeforeIncreasingDimValue(15);

				outputGroupAddressesForActorSwitchingOnAndOff("5/3/8", "5/3/9");
				outputGroupAddressesForVisualisationStatusFeedback("15/0/0");
				inputGroupAddressForOnAndOff("5/3/6");
				inputGroupAddressForDimStartAndStop("5/3/5");
				inputGroupAddressForAbsoluteDimValue("14/0/0");
			}
		}.build();
	}
}
