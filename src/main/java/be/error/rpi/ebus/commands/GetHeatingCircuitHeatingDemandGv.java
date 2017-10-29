package be.error.rpi.ebus.commands;

import java.util.List;

import be.error.rpi.ebus.EbusCommand;

/**
 * The heating controller does not seem to have a pure '0/1' register for indicating the heating demand of HC1.
 * We circumvent this by querying for the departure water temperature. This is 0 when there is no heating demand
 * and any value > 0 when there is heating demand
 */
public class GetHeatingCircuitHeatingDemandGv implements EbusCommand<Boolean> {

	@Override
	public String[] getEbusCommands() {
		return new String[] { "15b509030d2c01" };
	}

	public Boolean convertResult(List<String> result) {
		return !"0000".equals(result.get(0).substring(2, 6));
	}

	@Override
	public boolean withResult() {
		return true;
	}
}

