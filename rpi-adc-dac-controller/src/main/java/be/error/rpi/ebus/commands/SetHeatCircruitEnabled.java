package be.error.rpi.ebus.commands;

import static java.lang.String.format;

import java.util.List;

import be.error.rpi.ebus.EbusCommand;

/**
 * Enables or disables a heating circuit. If the circruit is disabled, it will not react on received temperatures and the heater/pump will not be operated (for example
 * in summer mode).
 */
public class SetHeatCircruitEnabled implements EbusCommand<Void> {

	private boolean enabled;

	public SetHeatCircruitEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String[] getEbusCommands() {
		return new String[] { format("15b509040e03000%s", enabled ? "1" : "0") };
	}

	@Override
	public Void convertResult(final List<String> results) {
		throw new IllegalStateException();
	}

	@Override
	public boolean withResult() {
		return false;
	}
}
