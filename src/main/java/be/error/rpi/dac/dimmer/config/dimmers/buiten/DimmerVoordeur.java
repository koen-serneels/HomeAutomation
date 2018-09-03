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

import static be.error.types.LocationId.VOORDEUR;

import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerBuilder;
import be.error.rpi.dac.dimmer.config.DimmerConfig;

/**
 * Created by koen on 01.10.16.
 */
public class DimmerVoordeur implements DimmerConfig {

	@Override
	public Dimmer start() throws Exception {
		return new DimmerBuilder() {
			{
				name(VOORDEUR);
				boardAddress(0x5B);
				boardChannel(1);
				delayBeforeIncreasingDimValue(0);

				outputGroupAddressesForActorSwitchingOnAndOff("1/1/0");
				outputGroupAddressesForVisualisationStatusFeedback("15/0/17");
				//outputGroupAddressesForSwitchLedControl();  not needed as no physical button
				//outputGroupAddressesSwitchUpdate();		not needed as no physical button

				precenseOperated("1/1/6");

				inputGroupAddressForAbsoluteDimValue("14/0/8");
				inputGroupAddressForAbsoluteDimValueOverride("14/0/21");
			}
		}.build();
	}
}
