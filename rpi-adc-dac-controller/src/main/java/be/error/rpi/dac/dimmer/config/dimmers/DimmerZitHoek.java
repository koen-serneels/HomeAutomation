package be.error.rpi.dac.dimmer.config.dimmers;

import static be.error.rpi.dac.dimmer.config.DimmerName.ZITHOEK;

import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;

/**
 * @author Koen Serneels
 */
public class DimmerZitHoek implements DimmerConfig {

	@Override
	public Dimmer start() throws Exception {
		return new DimmerBuilder() {
			{
				name(ZITHOEK);
				ic2BoardAddress(0x5B);
				boardChannel(3);
				delayBeforeIncreasingDimValue(15);

				outputGroupAddressesForActorSwitchingOnAndOff("5/3/8", "5/3/9");
				outputGroupAddressesForVisualisationStatusFeedback("15/0/0");
				outputGroupAddressesForSwitchLedControl("5/3/4");
				outputSwitchUpdateGroupAddresses("5/3/6");
				inputGroupAddressForOnAndOff("5/3/6");
				inputGroupAddressForDimStartAndStop("5/3/5");
				inputGroupAddressForAbsoluteDimValue("14/0/0");
			}
		}.build();
	}
}
