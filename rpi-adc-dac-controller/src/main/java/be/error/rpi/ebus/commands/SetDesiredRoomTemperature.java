package be.error.rpi.ebus.commands;

import static be.error.rpi.ebus.Support.addTemperatureToCommand;

import be.error.rpi.ebus.EbusCommand;

public class SetDesiredRoomTemperature implements EbusCommand {

	private String temperature;

	public SetDesiredRoomTemperature(final String temperature) {
		this.temperature = temperature;
	}

	@Override
	public String[] getCommands() {
		return new String[] { "15b509040e230001", addTemperatureToCommand("15b509050e2400%s%s", temperature, 0), "15b509070e250028014000" };
	}
}
