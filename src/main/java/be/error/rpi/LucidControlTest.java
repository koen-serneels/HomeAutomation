package be.error.rpi;

import lucidio.LucidControlAO4;
import lucidio.ValueVOS2;
import lucidio.ValueVOS4;

/**
 * Created by koen on 30.04.17.
 */
public class LucidControlTest {

	public static void main(String[] args) throws Exception {
		LucidControlAO4 lucidControlAO4 = new LucidControlAO4("/dev/ttyACM0");
		lucidControlAO4.open();

		for (double i = 0; i <= 10; i = i + 0.1) {
			lucidControlAO4.setIo(0, new ValueVOS2(i));
		}
		ValueVOS4 val = new ValueVOS4();
		lucidControlAO4.getIo(0, val);
		System.err.println("Value after:" + val.getVoltage());
	}
}
