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
package be.error.rpi.ebus.commands;

import java.util.List;

import be.error.rpi.ebus.EbusCommand;

/**
 * The heating controller does not seem to have a pure '0/1' register for indicating the heating demand of HC1.
 * We circumvent this by querying for the departure water temperature. This is 0 when there is no heating demand
 * and any value > 0 when there is heating demand
 */
public class GetHeatingCircuitHeatingDemandGv implements EbusCommand<Boolean> {

	@Override
	public String[] getEbusCommands() {
		return new String[] { "15b509030d2c01" };
	}

	public Boolean convertResult(List<String> result) {
		return !"0000".equals(result.get(0).substring(2, 6));
	}

	@Override
	public boolean withResult() {
		return true;
	}
}

