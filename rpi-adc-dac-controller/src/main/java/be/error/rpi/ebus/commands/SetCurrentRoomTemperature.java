package be.error.rpi.ebus.commands;

import static be.error.rpi.ebus.Support.addTemperatureToCommand;

import java.util.List;

import be.error.rpi.ebus.EbusCommand;

public class SetCurrentRoomTemperature implements EbusCommand<Void> {

	private String temperature;

	public SetCurrentRoomTemperature(final String temperature) {
		this.temperature = temperature;
	}

	@Override
	public String[] getEbusCommands() {
		return new String[] { addTemperatureToCommand("15b509050e3b00%s%s", "" + temperature, 0) };
	}

	@Override
	public Void convertResult(final List<String> results) {
		throw new IllegalStateException();
	}
}
