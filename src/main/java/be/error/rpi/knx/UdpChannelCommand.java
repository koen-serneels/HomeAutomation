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
package be.error.rpi.knx;

import static be.error.rpi.knx.UdpChannelCommand.Constants.TEMPERATURE;
import static be.error.types.LocationId.BADKAMER;
import static be.error.types.LocationId.DRESSING;
import static be.error.types.LocationId.GELIJKVLOERS;
import static be.error.types.LocationId.SK1;
import static be.error.types.LocationId.SK2;
import static be.error.types.LocationId.SK3;

public enum UdpChannelCommand {

	VENTILATIE("VENT"),
	HEATING_ENABLED("HEATING_ENABLED"),
	TEMPERATURE_BADKAMER(TEMPERATURE + "_" + BADKAMER),
	TEMPERATURE_DRESSING(TEMPERATURE + "_" + DRESSING),
	TEMPERATURE_SK1(TEMPERATURE + "_" + SK1),
	TEMPERATURE_SK2(TEMPERATURE + "_" + SK2),
	TEMPERATURE_SK3(TEMPERATURE + "_" + SK3),
	TEMPERATURE_GV(TEMPERATURE + "_" + GELIJKVLOERS);

	private String command;

	UdpChannelCommand(String s) {
		this.command = s;
	}

	public static UdpChannelCommand fromString(String s) {
		for (UdpChannelCommand udpChannelCommand : UdpChannelCommand.values()) {
			if (udpChannelCommand.command.equals(s)) {
				return udpChannelCommand;
			}
		}
		throw new IllegalStateException("Unknown command " + s);
	}

	public static class Constants {
		public static final String TEMPERATURE = "TEMP";
	}

}
