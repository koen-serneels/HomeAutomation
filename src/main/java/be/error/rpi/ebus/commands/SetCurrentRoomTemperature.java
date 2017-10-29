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

import static be.error.rpi.ebus.Support.addTemperatureToCommand;

import java.math.BigDecimal;
import java.util.List;

import be.error.rpi.ebus.EbusCommand;

public class SetCurrentRoomTemperature implements EbusCommand<Void> {

	private final BigDecimal temperatureControlValue;
	private final BigDecimal temperatureDisplayValue;

	public SetCurrentRoomTemperature(final BigDecimal temperatureControlValue, final BigDecimal temperatureDisplayValue) {
		this.temperatureControlValue = temperatureControlValue;
		this.temperatureDisplayValue = temperatureDisplayValue;
	}

	@Override
	public String[] getEbusCommands() {
		try {
			return new String[] { addTemperatureToCommand("15b509060e3a00%s%s00", temperatureControlValue, 2),
					addTemperatureToCommand("15b509050e3b00%s%s", temperatureDisplayValue, 0) };
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Void convertResult(final List<String> results) {
		throw new IllegalStateException();
	}
}
