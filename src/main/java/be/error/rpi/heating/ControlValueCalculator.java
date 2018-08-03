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
package be.error.rpi.heating;

import java.math.BigDecimal;

public class ControlValueCalculator {

	private BigDecimal delta_low = new BigDecimal("0.10");
	private BigDecimal delta_high = new BigDecimal("0.10");

	private BigDecimal delta_trigger = new BigDecimal("0.50");

	public BigDecimal getControlValue(BigDecimal currentTemp, BigDecimal desiredTemp) {
		if (currentTemp.compareTo(desiredTemp.subtract(delta_low)) < 0 && currentTemp.compareTo(desiredTemp.subtract(new BigDecimal("0.50"))) >= 0) {
			return desiredTemp.subtract(delta_trigger);
		}

		if (currentTemp.compareTo(desiredTemp.subtract(delta_high)) >= 0 && currentTemp.compareTo(desiredTemp.add(delta_high)) <= 0) {
			return desiredTemp.subtract(delta_high);
		}

		return currentTemp;
	}

	public BigDecimal getResetControlValue(boolean heatingDemand, BigDecimal desiredTemp) {
		if (heatingDemand) {
			return desiredTemp.subtract(delta_trigger);
		} else {
			return desiredTemp.add(delta_trigger);
		}
	}

	public RoomTemperature updateHeatingDemand(RoomTemperature roomTemperature) {
		BigDecimal currentTemp = roomTemperature.getCurrentTemp();
		BigDecimal desiredTemp = roomTemperature.getDesiredTemp();

		//No state yet. In this case we simply check if the current temp vs required temp + delta_high. If lower or equal,enable heating demand. If higher, disable
		// heating demand
		if (roomTemperature.getHeatingDemand() == null) {
			roomTemperature.updateHeatingDemand(currentTemp.compareTo(desiredTemp.add(delta_high)) <= 0);
			return roomTemperature;
		}

		//If state is present, we only toggle the heating demand if a boundary is crossed
		if (currentTemp.compareTo(desiredTemp.subtract(delta_low)) < 0) {
			roomTemperature.updateHeatingDemand(true);
			return roomTemperature;
		}

		if (currentTemp.compareTo(desiredTemp.add(delta_high)) > 0) {
			roomTemperature.updateHeatingDemand(false);
			return roomTemperature;
		}

		return roomTemperature;
	}
}
