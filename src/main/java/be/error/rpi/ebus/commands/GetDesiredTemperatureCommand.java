package be.error.rpi.ebus.commands;

import static be.error.rpi.ebus.Support.decodeDATA2c;
import static org.apache.commons.codec.binary.Hex.decodeHex;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.codec.DecoderException;

import be.error.rpi.ebus.EbusCommand;

public class GetDesiredTemperatureCommand implements EbusCommand<BigDecimal> {

	@Override
	public String[] getEbusCommands() {
		return new String[] { "15b50903292200" };
	}

	public BigDecimal convertResult(List<String> result) {
		String hex = result.get(0).substring(8, 10) + result.get(0).substring(6, 8);
		try {
			return decodeDATA2c(decodeHex(hex.toCharArray()));
		} catch (DecoderException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean withResult() {
		return true;
	}
}
