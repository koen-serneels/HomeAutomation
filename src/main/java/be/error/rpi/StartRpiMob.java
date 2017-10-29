/*-
 * #%L
 * Home Automation
 * %%
 * Copyright (C) 2016 - 2017 Koen Serneels
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
