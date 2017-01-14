package be.error.rpi.ebus.commands;

import static be.error.rpi.ebus.Support.addTemperatureToCommand;

import java.math.BigDecimal;
import java.util.List;

import be.error.rpi.ebus.EbusCommand;
import be.error.rpi.ebus.EbusdTcpCommunicatorImpl;

public class SetCurrentRoomTemperature implements EbusCommand<Void> {

	private String temperature;
	private BigDecimal delta = new BigDecimal("0.12");

	public SetCurrentRoomTemperature(final String temperature) {
		this.temperature = temperature;
	}

	@Override
	public String[] getEbusCommands() {
		try {
			return new String[] { addTemperatureToCommand("15b509060e3a00%s%s00",
					"" + new ControlValueCalculator(new EbusdTcpCommunicatorImpl()).getCurrentControlValue(temperature).toString(), 2),
					addTemperatureToCommand("15b509050e3b00%s%s", "" + temperature, 0) };
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Void convertResult(final List<String> results) {
		throw new IllegalStateException();
	}

	/*private BigDecimal getControlValue() {
		BigDecimal current = new BigDecimal(temperature).setScale(2, RoundingMode.HALF_UP);

		BigDecimal base = current.setScale(0, BigDecimal.ROUND_DOWN).setScale(2);
		BigDecimal baseHalf = base.add((new BigDecimal("0.50"))).setScale(2);

		BigDecimal newCurrent = current;
		if (current.compareTo(base.add(delta)) < 0) {
			newCurrent = base;
		} else if (current.compareTo(baseHalf) > 0 && current.compareTo(baseHalf.add(delta)) < 0) {
			newCurrent = baseHalf;
		}

		return newCurrent;
	}*/
}
