package be.error.rpi.ebus.commands;

import static be.error.rpi.ebus.Support.addTemperatureToCommand;

import java.math.BigDecimal;
import java.util.List;

import be.error.rpi.ebus.EbusCommand;

public class SetDesiredRoomTemperature implements EbusCommand<Void> {

	private BigDecimal temperature;

	public SetDesiredRoomTemperature(final BigDecimal temperature) {
		this.temperature = temperature;
	}

	@Override
	public String[] getEbusCommands() {
		return new String[] { addTemperatureToCommand("15b509050e2200%s%s", temperature, 0) };
	}

	@Override
	public Void convertResult(final List<String> results) {
		throw new IllegalStateException();
	}
}
