package be.error.rpi.ebus.commands;

import static be.error.rpi.ebus.Support.decodeDATA2c;
import static org.apache.commons.codec.binary.Hex.decodeHex;

import java.util.List;

import org.apache.commons.codec.DecoderException;

import be.error.rpi.ebus.EbusCommand;

public class GetDepartWaterTemperatureGv implements EbusCommand<String> {

	@Override
	public String[] getEbusCommands() {
		return new String[] { "15b509030d2c01" };
	}

	public String convertResult(List<String> result) {
		String hex = result.get(0).substring(4, 6) + result.get(0).substring(2, 4);
		try {
			return decodeDATA2c(decodeHex(hex.toCharArray())).toString();
		} catch (DecoderException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean withResult() {
		return true;
	}
}

