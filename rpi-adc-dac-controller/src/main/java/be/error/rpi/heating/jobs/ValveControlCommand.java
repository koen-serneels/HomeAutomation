package be.error.rpi.heating.jobs;

import be.error.rpi.heating.RoomTemperature;

public class ValveControlCommand {

	private RoomTemperature roomTemperature;
	private boolean heatingCircuitStatus;

	public ValveControlCommand(final RoomTemperature roomTemperature, final boolean heatingCircuitStatus) {
		this.roomTemperature = roomTemperature;
		this.heatingCircuitStatus = heatingCircuitStatus;
	}

	public RoomTemperature getRoomTemperature() {
		return roomTemperature;
	}

	public boolean isHeatingCircuitStatus() {
		return heatingCircuitStatus;
	}
}
