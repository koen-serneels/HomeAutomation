/*-
 * #%L
 * Home Automation
 * %%
 * Copyright (C) 2016 - 2017 Koen Serneels
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package be.error.rpi.dac.dimmer.config.dimmers.gv;

import static be.error.types.LocationId.ZITHOEK;

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
				outputGroupAddressesSwitchUpdate("5/3/6");
				inputGroupAddressForOnAndOff("5/3/6");
				inputGroupAddressForDimStartAndStop("5/3/5");
				inputGroupAddressForAbsoluteDimValue("14/0/0");
			}
		}.build();
	}
}
