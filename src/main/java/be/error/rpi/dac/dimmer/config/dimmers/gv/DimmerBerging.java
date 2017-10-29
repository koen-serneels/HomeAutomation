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

import static be.error.types.LocationId.BERGING;

import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;

/**
 * Created by koen on 03.11.16.
 */
public class DimmerBerging implements DimmerConfig {

	@Override
	public Dimmer start() throws Exception {
		return new DimmerBuilder() {
			{
				name(BERGING);
				ic2BoardAddress(0x59);
				boardChannel(1);
				delayBeforeIncreasingDimValue(15);

				outputGroupAddressesForVisualisationStatusFeedback("15/0/11");
				outputGroupAddressesForActorSwitchingOnAndOff("5/6/1");
				outputGroupAddressesSwitchUpdate("5/6/5");

				inputGroupAddressForOnAndOff("5/6/0");
				inputGroupAddressForOnOffOverride("5/6/4", "5/6/6");
				inputGroupAddressForAbsoluteDimValue("14/0/11");
				inputGroupAddressForDimStartAndStop("5/6/3");
			}
		}.build();
	}
}
