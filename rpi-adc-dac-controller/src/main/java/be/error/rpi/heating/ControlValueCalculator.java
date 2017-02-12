package be.error.rpi.heating;

import java.math.BigDecimal;

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

	public BigDecimal getResetControlValue(boolean heatingDemand, BigDecimal desiredTemp) {
		if (heatingDemand) {
			return desiredTemp.subtract(delta_trigger);
		} else {
			return desiredTemp;
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