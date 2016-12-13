package be.error.rpi.dac.dimmer.config.dimmers.gv;

import static be.error.rpi.dac.dimmer.config.DimmerName.GARAGE;

import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;

public class DimmerGarage implements DimmerConfig {

	@Override
	public Dimmer start() throws Exception {
		return new DimmerBuilder() {
			{
				name(GARAGE);
				ic2BoardAddress(0x58);
				boardChannel(0);
				delayBeforeIncreasingDimValue(15);

				outputGroupAddressesForActorSwitchingOnAndOff("5/7/5");
				outputGroupAddressesForVisualisationStatusFeedback("15/0/12");
				outputGroupAddressesSwitchUpdate("5/7/1");
				inputGroupAddressForOnAndOff("5/7/0");
				inputGroupAddressForOnOffOverride("5/7/2", "5/7/6");

				inputGroupAddressForDimStartAndStop("5/7/3");
				inputGroupAddressForAbsoluteDimValue("14/0/12");
			}
		}.build();
	}
}