package be.error.rpi.ebus.commands;

import static be.error.rpi.ebus.Support.addTemperatureToCommand;

import java.math.BigDecimal;
import java.util.List;

import be.error.rpi.ebus.EbusCommand;

public class SetCurrentRoomTemperature implements EbusCommand<Void> {

	private final BigDecimal temperatureControlValue;
	private final BigDecimal temperatureDisplayValue;

	public SetCurrentRoomTemperature(final BigDecimal temperatureControlValue, final BigDecimal temperatureDisplayValue) {
		this.temperatureControlValue = temperatureControlValue;
		this.temperatureDisplayValue = temperatureDisplayValue;
	}

	@Override
	public String[] getEbusCommands() {
		try {
			return new String[] { addTemperatureToCommand("15b509060e3a00%s%s00", temperatureControlValue, 2),
					addTemperatureToCommand("15b509050e3b00%s%s", temperatureDisplayValue, 0) };
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Void convertResult(final List<String> results) {
		throw new IllegalStateException();
	}
}
