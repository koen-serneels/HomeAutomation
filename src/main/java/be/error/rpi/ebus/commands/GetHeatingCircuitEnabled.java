package be.error.rpi.ebus.commands;

import java.util.List;

import be.error.rpi.ebus.EbusCommand;

public class GetHeatingCircuitEnabled implements EbusCommand<Boolean> {

	@Override
	public String[] getEbusCommands() {
		return new String[] { "15b50903290300" };
	}

	public Boolean convertResult(List<String> result) {
		return result.get(0).charAt(7) == '1';
	}

	@Override
	public boolean withResult() {
		return true;
	}
}

