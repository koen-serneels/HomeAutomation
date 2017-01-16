package be.error.rpi.heating;

import static java.math.BigDecimal.ROUND_FLOOR;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

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

	private boolean heatingDemand;

	public RoomTemperature(final LocationId roomId) {
		this.roomId = roomId;
	}

	public RoomTemperature(final LocationId roomId, final BigDecimal currentTemp, final BigDecimal desiredTemp) {
		this.roomId = roomId;
		this.currentTemp = currentTemp;
		this.desiredTemp = desiredTemp;
	}

	public void updateCurrentTemp(Double currentTemp) {
		this.currentTemp = new BigDecimal(currentTemp).setScale(2, ROUND_FLOOR);
	}

	public void updateDesiredTemp(String desiredTemp) {
		this.desiredTemp = new BigDecimal(desiredTemp).setScale(2, ROUND_FLOOR);
	}

	public void updateHeatingDemand(final boolean heatingDemand) {
		this.heatingDemand = heatingDemand;
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

	public boolean isHeatingDemand() {
		return heatingDemand;
	}

	public RoomTemperature clone() {
		return SerializationUtils.clone(this);
	}

	@Override
	public String toString() {
		return reflectionToString(this);
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
