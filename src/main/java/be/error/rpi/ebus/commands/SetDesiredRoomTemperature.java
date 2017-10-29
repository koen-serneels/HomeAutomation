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
import be.error.rpi.heating.EbusDeviceAddress;

public class SetDesiredRoomTemperature implements EbusCommand<Void> {

	/**
	 * 18
	 * --
	 * 3015b509040e 2300 01 / 00
	 * 3015b509050e 2400 2001 / 00
	 * 3015b509070e 2500 86734200 / 00
	 * <p>
	 * <p>
	 * 3015b509040e230001 / 00
	 * 3015b509050e 2400 1001 / 00
	 * 3015b509070e 2500 86734200 / 00
	 */
	private final BigDecimal temperature;
	private final EbusDeviceAddress ebusDeviceAddress;

	public SetDesiredRoomTemperature(final BigDecimal temperature, final EbusDeviceAddress ebusDeviceAddress) {
		this.temperature = temperature;
		this.ebusDeviceAddress = ebusDeviceAddress;
	}

	@Override
	public String[] getEbusCommands() {
		return new String[] { "15b509040e230001", addTemperatureToCommand("15b509050e2400%s%s", temperature, 0), "15b509070e250086734200" };
	}

	//
	@Override
	public Void convertResult(final List<String> results) {
		throw new IllegalStateException();
	}

	/*
	private String getCommand() {
		if (ebusDeviceAddress == FIRST_FLOOR) {
			return "15b509050e2200%s%s";
		} else if (ebusDeviceAddress == GROUND_FLOOR) {
		return "15b509050e2400%s%s";
		.IllegalStateException} else {
			throw new IllegalStateException("Ebus device:" + ebusDeviceAddress + " not supported by this command");
		}
	}*/
}
