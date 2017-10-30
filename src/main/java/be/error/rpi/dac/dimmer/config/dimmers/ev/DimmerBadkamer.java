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
package be.error.rpi.dac.dimmer.config.dimmers.ev;

import static be.error.types.LocationId.BADKAMER;

import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;

/**
 * @author Koen Serneels
 */

public class DimmerBadkamer implements DimmerConfig {

	@Override
	public Dimmer start() throws Exception {
		return new DimmerBuilder() {
			{
				name(BADKAMER);
				boardAddress(0x5B);
				boardChannel(3);

				outputGroupAddressesForVisualisationStatusFeedback("15/0/2");
				outputGroupAddressesForActorSwitchingOnAndOff("4/4/1");
				outputGroupAddressesSwitchUpdate("4/4/10");

				//inputGroupAddressForOnAndOff("4/4/8");
				inputGroupAddressForOnOffOverride("4/4/8", "4/4/11");
				inputGroupAddressForDimStartAndStop("4/4/9");
				inputGroupAddressForAbsoluteDimValue("14/0/2");
				inputGroupAddressForAbsoluteDimValueOverride("14/0/16");
			}
		}.build();
	}
}
