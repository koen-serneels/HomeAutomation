package be.error.rpi.heating.jobs;

import java.io.Serializable;
import java.util.Map;

import tuwien.auto.calimero.GroupAddress;

import be.error.rpi.ebus.EbusCommand;
import be.error.rpi.heating.EbusDeviceAddress;

public class JobConfig implements Serializable {

	private EbusDeviceAddress ebusDeviceAddress;
	private Map<EbusCommand<?>, GroupAddress> ebusCommands;

	public JobConfig(final EbusDeviceAddress ebusDeviceAddress, final Map<EbusCommand<?>, GroupAddress> ebusCommands) {
		this.ebusDeviceAddress = ebusDeviceAddress;
		this.ebusCommands = ebusCommands;
	}

	public EbusDeviceAddress getEbusDeviceAddress() {
		return ebusDeviceAddress;
	}

	public Map<EbusCommand<?>, GroupAddress> getEbusCommands() {
		return ebusCommands;
	}
}
