package be.error.rpi;

import static com.pi4j.io.i2c.I2CBus.BUS_1;

import java.math.BigDecimal;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

/**
 * Created by koen on 18.10.16.
 */
public class StartRpiMob {

	public static void main(String[] args) throws Exception {
		I2CBus bus = I2CFactory.getInstance(BUS_1);
		I2CDevice i2CDeviceOne = bus.getDevice(0x58);
		System.err.println("TOET");
		DatagramSocket serverSocket = new DatagramSocket(5000);
		byte[] receiveData = new byte[4];

		while (true) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);

			for (int i = 0; i < 4; i++) {
				BigDecimal b = new BigDecimal(Byte.toUnsignedInt(receiveData[i]));
				System.err.println(b);
				byte[] toSend = convert(b);
				i2CDeviceOne.write(new byte[] { (byte) i, toSend[1], toSend[0] }, 0, 3);
				Thread.sleep(100);
			}
		}
	}

	private static byte[] convert(BigDecimal bigDecimal) {
		byte[] b = new BigDecimal(1023).divide(new BigDecimal(255), 2, BigDecimal.ROUND_HALF_UP).multiply(bigDecimal).toBigInteger().toByteArray();
		byte[] r = new byte[0];
		if (b.length == 1) {
			b = new byte[] { 0, b[0] };
		}
		return b;
	}
}
