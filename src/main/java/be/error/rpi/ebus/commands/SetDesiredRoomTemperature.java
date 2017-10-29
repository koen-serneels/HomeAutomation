package be.error.rpi.ebus.commands;

import static be.error.rpi.ebus.Support.addTemperatureToCommand;

import java.math.BigDecimal;
import java.util.List;

import be.error.rpi.ebus.EbusCommand;
import be.error.rpi.heating.EbusDeviceAddress;

public class SetDesiredRoomTemperature implements EbusCommand<Void> {

	/**
	 * 18
	 * --
	 * 3015b509040e 2300 01 / 00
	 * 3015b509050e 2400 2001 / 00
	 * 3015b509070e 2500 86734200 / 00
	 * <p>
	 * <p>
	 * 3015b509040e230001 / 00
	 * 3015b509050e 2400 1001 / 00
	 * 3015b509070e 2500 86734200 / 00
	 */
	private final BigDecimal temperature;
	private final EbusDeviceAddress ebusDeviceAddress;

	public SetDesiredRoomTemperature(final BigDecimal temperature, final EbusDeviceAddress ebusDeviceAddress) {
		this.temperature = temperature;
		this.ebusDeviceAddress = ebusDeviceAddress;
	}

	@Override
	public String[] getEbusCommands() {
		return new String[] { "15b509040e230001", addTemperatureToCommand("15b509050e2400%s%s", temperature, 0), "15b509070e250086734200" };
	}

	//
	@Override
	public Void convertResult(final List<String> results) {
		throw new IllegalStateException();
	}

	/*
	private String getCommand() {
		if (ebusDeviceAddress == FIRST_FLOOR) {
			return "15b509050e2200%s%s";
		} else if (ebusDeviceAddress == GROUND_FLOOR) {
		return "15b509050e2400%s%s";
		.IllegalStateException} else {
			throw new IllegalStateException("Ebus device:" + ebusDeviceAddress + " not supported by this command");
		}
	}*/
}
