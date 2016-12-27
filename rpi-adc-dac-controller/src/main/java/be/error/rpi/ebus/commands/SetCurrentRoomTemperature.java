package be.error.rpi.ebus.commands;

import static be.error.rpi.ebus.Support.addTemperatureToCommand;

import be.error.rpi.ebus.EbusCommand;

public class SetCurrentRoomTemperature implements EbusCommand {

	private String temperature;

	public SetCurrentRoomTemperature(final String temperature) {
		this.temperature = temperature;
	}

	@Override
	public String[] getCommands() {
		return new String[] { addTemperatureToCommand("15b509060e3a00%s%s00", "" + temperature, 2) };
	}
}
