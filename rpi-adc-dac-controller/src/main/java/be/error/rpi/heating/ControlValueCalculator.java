package be.error.rpi.heating;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.math.BigDecimal;
import java.util.Optional;

public class ControlValueCalculator {

	private BigDecimal delta_low = new BigDecimal("0.20");
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

	public Optional<Boolean> hasHeatingDemand(RoomTemperature roomTemperature) {
		BigDecimal controlValue = getControlValue(roomTemperature.getCurrentTemp(), roomTemperature.getDesiredTemp());

		if (controlValue.compareTo(roomTemperature.getDesiredTemp().subtract(delta_trigger)) <= 0) {
			return of(true);
		}

		if (controlValue.compareTo(roomTemperature.getDesiredTemp()) >= 0) {
			return of(false);
		}

		return empty();
	}
}