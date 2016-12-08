package be.error.rpi.test;

import static com.pi4j.io.i2c.I2CBus.BUS_1;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

/**
 * Created by koen on 01.07.16.
 */
public class TestMe {

	public static void main(String[] args) throws Exception {
		I2CBus bus = I2CFactory.getInstance(BUS_1);
		//58 59 5B
		final I2CDevice i2CDeviceOne = bus.getDevice(0x59);

		byte[] value = convert(new BigDecimal((0)));

		i2CDeviceOne.write(new byte[] { (byte) 1, value[1], value[0] }, 0, 3);
		Thread.sleep(100);
	}

	private static byte[] convert(BigDecimal bigDecimal) {
		BigDecimal result = new BigDecimal(1023).divide(new BigDecimal(100)).multiply(bigDecimal).setScale(0, RoundingMode.HALF_UP);
		byte[] b = result.toBigInteger().toByteArray();
		byte[] r = new byte[0];
		if (b.length == 1) {
			return new byte[] { 0, b[0] };
		}
		return b;
	}
}

