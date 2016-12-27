package be.error.rpi.ebus;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.substring;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by koen on 26.12.16.
 */
public class Support {

	public static String addTemperatureToCommand(String command, String temperature, int offset) {
		BigDecimal bigDecimal = new BigDecimal(temperature).setScale(2, RoundingMode.HALF_UP).add(new BigDecimal(offset));
		bigDecimal = bigDecimal.multiply(new BigDecimal(16)).setScale(0, RoundingMode.HALF_UP);
		String converted = leftPad(bigDecimal.toBigInteger().toString(16), 4, "0");
		return format(command, substring(converted, 2, 4), substring(converted, 0, 2));
	}
}
