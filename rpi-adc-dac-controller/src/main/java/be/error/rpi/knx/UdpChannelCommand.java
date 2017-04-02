package be.error.rpi.knx;

import static be.error.rpi.knx.UdpChannelCommand.Constants.TEMPERATURE;
import static be.error.types.LocationId.BADKAMER;
import static be.error.types.LocationId.DRESSING;
import static be.error.types.LocationId.SK1;
import static be.error.types.LocationId.SK2;
import static be.error.types.LocationId.SK3;

public enum UdpChannelCommand {

	VENTILATIE("VENT"),
	HEATING_ENABLED("HEATING_ENABLED"),
	TEMPERATURE_BADKAMER(TEMPERATURE + "_" + BADKAMER),
	TEMPERATURE_DRESSING(TEMPERATURE + "_" + DRESSING),
	TEMPERATURE_SK1(TEMPERATURE + "_" + SK1),
	TEMPERATURE_SK2(TEMPERATURE + "_" + SK2),
	TEMPERATURE_SK3(TEMPERATURE + "_" + SK3);

	private String command;

	UdpChannelCommand(String s) {
		this.command = s;
	}

	public static UdpChannelCommand fromString(String s) {
		for (UdpChannelCommand udpChannelCommand : UdpChannelCommand.values()) {
			if (udpChannelCommand.command.equals(s)) {
				return udpChannelCommand;
			}
		}
		throw new IllegalStateException("Unknown command " + s);
	}

	public static class Constants {
		public static final String TEMPERATURE = "TEMP";
	}

}
