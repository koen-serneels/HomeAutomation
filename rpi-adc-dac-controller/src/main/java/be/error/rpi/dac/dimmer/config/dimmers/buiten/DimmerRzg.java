package be.error.rpi.dac.dimmer.config.dimmers.buiten;

import static be.error.types.LocationId.RZG;

import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;

public class DimmerRzg implements DimmerConfig {

	@Override
	public Dimmer start() throws Exception {
		return new DimmerBuilder() {
			{
				name(RZG);
				ic2BoardAddress(0x5A);
				boardChannel(0);
				delayBeforeIncreasingDimValue(0);

				outputGroupAddressesForVisualisationStatusFeedback("15/0/14");
				outputGroupAddressesForActorSwitchingOnAndOff("1/3/0");
				outputGroupAddressesSwitchUpdate("1/3/5", "1/3/2");

				//Not needed inputGroupAddressForOnAndOff("1/3/0");
				//Not needed:inputGroupAddressForDimStartAndStop("1/2/3");
				//inputGroupAddressForOnOffOverride("1/3/4", "2/3/0");
				inputGroupAddressForAbsoluteDimValue("14/0/17");
			}
		}.build();
	}
}