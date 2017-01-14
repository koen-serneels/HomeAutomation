package be.error.rpi.ebus;

import static java.lang.String.format;
import static java.math.BigDecimal.ROUND_FLOOR;
import static java.math.RoundingMode.HALF_UP;
import static org.apache.commons.codec.binary.Hex.encodeHex;
import static org.apache.commons.lang3.StringUtils.substring;

import java.math.BigDecimal;

public class Support {

	public static String addTemperatureToCommand(String command, String temperature, int offset) {
		String converted = encodeDATA2c(new BigDecimal(temperature).setScale(2, ROUND_FLOOR).add(new BigDecimal(offset)));
		return format(command, substring(converted, 2, 4), substring(converted, 0, 2));
	}

	public static BigDecimal decodeDATA2c(byte[] data) {
		return new BigDecimal(decodeInt(data)).divide(new BigDecimal(16)).setScale(2, HALF_UP);
	}

	private static short decodeInt(byte[] data) {
		return (short) (data[0] << 8 | data[1] & 0xFF);
	}

	public static String encodeDATA2c(BigDecimal data) {
		return new String(encodeHex(encodeInt((short) (data.multiply(new BigDecimal(16)).setScale(0, ROUND_FLOOR).floatValue()))));
	}

	private static byte[] encodeInt(short data) {
		return new byte[] { (byte) (data >> 8), (byte) data };
	}


}
