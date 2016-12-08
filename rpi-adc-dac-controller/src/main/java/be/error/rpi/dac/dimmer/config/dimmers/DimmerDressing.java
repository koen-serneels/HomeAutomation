package be.error.rpi.dac.dimmer.config.dimmers;

import static be.error.rpi.dac.dimmer.config.DimmerName.DRESSING;

import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;
import be.error.rpi.dac.dimmer.config.DimmerName;

/**
 * @author Koen Serneels
 */
public class DimmerDressing implements DimmerConfig{

	@Override
	public Dimmer start() throws Exception {
		return  new DimmerBuilder() {
			{
				name(DRESSING);
				ic2BoardAddress(0x5B);
				boardChannel(2);
				outputGroupAddressesForVisualisationStatusFeedback("15/0/3");
				outputGroupAddressesForActorSwitchingOnAndOff("4/6/1");
				inputGroupAddressForAbsoluteDimValue("14/0/3");
			}
		}.build();
	}
}
