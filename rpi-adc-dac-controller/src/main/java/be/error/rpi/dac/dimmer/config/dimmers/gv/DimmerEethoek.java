package be.error.rpi.dac.dimmer.config.dimmers.gv;

import static be.error.rpi.dac.dimmer.config.DimmerName.EETHOEK;

import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;

/**
 * Created by koen on 01.10.16.
 */
public class DimmerEethoek implements DimmerConfig {

	@Override
	public Dimmer start() throws Exception {
		return new DimmerBuilder() {
			{
				name(EETHOEK);
				ic2BoardAddress(0x5B);
				boardChannel(0);
				delayBeforeIncreasingDimValue(15);

				outputGroupAddressesForActorSwitchingOnAndOff("5/1/8", "5/1/7");
				outputGroupAddressesForVisualisationStatusFeedback("15/0/6");
				outputGroupAddressesForSwitchLedControl("5/1/4");
				outputGroupAddressesSwitchUpdate("5/1/6");
				inputGroupAddressForOnAndOff("5/1/6");
				inputGroupAddressForDimStartAndStop("5/1/5");
				inputGroupAddressForAbsoluteDimValue("14/0/6");
			}
		}.build();
	}
}