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

import static java.math.BigDecimal.ROUND_FLOOR;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import be.error.types.LocationId;

public class RoomTemperature implements Serializable {

	private LocationId roomId;

	private BigDecimal currentTemp;
	private BigDecimal desiredTemp;
	private Boolean heatingDemand;

	public RoomTemperature(final LocationId roomId) {
		this.roomId = roomId;
	}

	public RoomTemperature(final LocationId roomId, final BigDecimal currentTemp, final BigDecimal desiredTemp) {
		this.roomId = roomId;
		this.currentTemp = currentTemp;
		this.desiredTemp = desiredTemp;
	}

	public void update(RoomTemperature roomTemperature) {
		this.currentTemp = roomTemperature.getCurrentTemp();
		this.desiredTemp = roomTemperature.getDesiredTemp();
		this.heatingDemand = roomTemperature.heatingDemand;
	}

	public void updateCurrentTemp(Double currentTemp) {
		this.currentTemp = new BigDecimal(currentTemp).setScale(2, ROUND_FLOOR);
	}

	public void updateDesiredTemp(String desiredTemp) {
		this.desiredTemp = new BigDecimal(desiredTemp).setScale(2, ROUND_FLOOR);
	}

	public void updateHeatingDemand(boolean b) {
		this.heatingDemand = b;
	}

	public BigDecimal getCurrentTemp() {
		return currentTemp;
	}

	public BigDecimal getDesiredTemp() {
		return desiredTemp;
	}

	public LocationId getRoomId() {
		return roomId;
	}

	public boolean ready() {
		return currentTemp != null && desiredTemp != null;
	}

	public BigDecimal delta() {
		return desiredTemp.subtract(currentTemp);
	}

	public RoomTemperature clone() {
		return SerializationUtils.clone(this);
	}

	public Boolean getHeatingDemand() {
		return heatingDemand;
	}

	@Override
	public String toString() {
		return reflectionToString(this, SHORT_PREFIX_STYLE);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		RoomTemperature that = (RoomTemperature) o;
		return new EqualsBuilder().append(roomId, that.roomId).append(currentTemp, that.currentTemp).append(desiredTemp, that.desiredTemp).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(roomId).append(currentTemp).append(desiredTemp).toHashCode();
	}
}
