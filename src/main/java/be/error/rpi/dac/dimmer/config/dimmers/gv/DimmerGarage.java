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

import static be.error.types.LocationId.GARAGE;

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
