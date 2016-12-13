package be.error.rpi.dac.dimmer.config.dimmers.buiten;

import static be.error.rpi.dac.dimmer.config.DimmerName.AG;

import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;

public class DimmerAg implements DimmerConfig {

	@Override
	public Dimmer start() throws Exception {
		return new DimmerBuilder() {
			{
				name(AG);
				ic2BoardAddress(0x5A);
				boardChannel(3);
				delayBeforeIncreasingDimValue(15);

				outputGroupAddressesForActorSwitchingOnAndOff("1/2/0");
				outputGroupAddressesForVisualisationStatusFeedback("15/0/13");
				outputGroupAddressesForSwitchLedControl("1/2/2");
				outputGroupAddressesSwitchUpdate("1/2/5");
				inputGroupAddressForOnAndOff("1/2/4");
				inputGroupAddressForDimStartAndStop("1/2/3");
				inputGroupAddressForAbsoluteDimValue("14/0/13");
			}
		}.build();
	}
}