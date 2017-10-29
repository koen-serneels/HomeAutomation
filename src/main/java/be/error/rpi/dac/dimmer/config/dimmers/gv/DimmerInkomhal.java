package be.error.rpi.dac.dimmer.config.dimmers.gv;

import static be.error.types.LocationId.INKOMHAL;

import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;

/**
 * Created by koen on 01.10.16.
 */
public class DimmerInkomhal implements DimmerConfig {

	@Override
	public Dimmer start() throws Exception {
		return new DimmerBuilder() {
			{
				name(INKOMHAL);
				ic2BoardAddress(0x59);
				boardChannel(0);
				delayBeforeIncreasingDimValue(15);

				outputGroupAddressesForVisualisationStatusFeedback("15/0/7");
				outputGroupAddressesForActorSwitchingOnAndOff("5/5/1");
				inputGroupAddressForAbsoluteDimValue("14/0/7");
			}
		}.build();
	}
}