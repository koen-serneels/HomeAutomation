package be.error.rpi.knx;

public enum UdpChannelCommand {
	VENTILATIE("VENT"),
	TEMPERATURE_EV("TEMP_EV");

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
}
