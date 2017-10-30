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
				boardAddress(0x5A);
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
