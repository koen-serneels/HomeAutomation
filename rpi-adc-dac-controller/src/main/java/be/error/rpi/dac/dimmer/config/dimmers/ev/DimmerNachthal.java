package be.error.rpi.dac.dimmer.config.dimmers.ev;

import static be.error.rpi.dac.dimmer.config.DimmerName.NACHTHAL;

import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;
import be.error.rpi.dac.dimmer.config.DimmerName;

/**
 * @author Koen Serneels
 */
public class DimmerNachthal implements DimmerConfig {

	@Override
	public Dimmer start() throws Exception {
		return new DimmerBuilder() {
			{
				name(NACHTHAL);
				ic2BoardAddress(0x5B);
				boardChannel(1);
				outputGroupAddressesForVisualisationStatusFeedback("15/0/4");
				outputGroupAddressesForActorSwitchingOnAndOff("4/7/1");
				inputGroupAddressForAbsoluteDimValue("14/0/4");
			}
		}.build();
	}
}
