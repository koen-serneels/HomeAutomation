package be.error.rpi.ebus.commands;

import java.math.BigDecimal;
import java.math.RoundingMode;

import be.error.rpi.ebus.EbusdTcpCommunicator;

/**
 * Created by koen on 04.01.17.
 */
public class ControlValueCalculator {

	private BigDecimal delta_low = new BigDecimal("0.20");
	private BigDecimal delta_high = new BigDecimal("0.10");

	private final EbusdTcpCommunicator ebusdTcpCommunicator;

	public ControlValueCalculator(final EbusdTcpCommunicator ebusdTcpCommunicator) {
		this.ebusdTcpCommunicator = ebusdTcpCommunicator;
	}

	public BigDecimal getCurrentControlValue(String current) throws Exception {
		BigDecimal currentTemp = new BigDecimal(current).setScale(2, RoundingMode.HALF_UP);
		GetDesiredTemperatureCommand getDesiredTemperatureCommand = new GetDesiredTemperatureCommand();
		BigDecimal desiredTemp = getDesiredTemperatureCommand.convertResult(ebusdTcpCommunicator.send(getDesiredTemperatureCommand));

		return decide(currentTemp, desiredTemp);
	}

	public BigDecimal decide(BigDecimal currentTemp, BigDecimal desiredTemp) {

		if (currentTemp.compareTo(desiredTemp.subtract(delta_low)) < 0 && currentTemp.compareTo(desiredTemp.subtract(new BigDecimal("0.50"))) >= 0) {
			return desiredTemp.subtract(new BigDecimal("0.50"));
		}

		if (currentTemp.compareTo(desiredTemp.subtract(delta_high)) >= 0 && currentTemp.compareTo(desiredTemp.add(delta_high)) <= 0) {
			return desiredTemp.subtract(delta_high);
		}

		return currentTemp;
	}
}
