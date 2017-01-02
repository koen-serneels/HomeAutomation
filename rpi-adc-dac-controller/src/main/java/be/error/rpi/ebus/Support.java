package be.error.rpi.ebus;

import static java.lang.String.format;
import static java.math.RoundingMode.HALF_UP;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.substring;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Support {

	public static String addTemperatureToCommand(String command, String temperature, int offset) {
		BigDecimal bigDecimal = new BigDecimal(temperature).setScale(2, RoundingMode.HALF_UP).add(new BigDecimal(offset));
		bigDecimal = bigDecimal.multiply(new BigDecimal(16)).setScale(0, RoundingMode.HALF_UP);
		String converted = leftPad(bigDecimal.toBigInteger().toString(16), 4, "0");
		return format(command, substring(converted, 2, 4), substring(converted, 0, 2));
	}

	public static BigDecimal decodeDATA2c(byte[] data) {
		return new BigDecimal(decodeInt(data)).divide(new BigDecimal(16)).setScale(2, HALF_UP);
	}

	private static short decodeInt(byte[] data) {
		return (short) (data[0] << 8 | data[1] & 0xFF);
	}
}
