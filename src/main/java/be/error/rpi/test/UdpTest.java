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
package be.error.rpi.test;

import static org.apache.commons.lang3.ArrayUtils.add;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by koen on 06.07.16.
 */
public class UdpTest {

	public static void main(String[] args) throws Exception {
		final InetAddress IPAddress = InetAddress.getByName("192.168.0.10");
		final DatagramSocket clientSocket = new DatagramSocket();

		new Thread() {
			@Override
			public void run() {
				try {
					while (true) {
						String s = "0:0:0:";
						DatagramPacket sendPacket = new DatagramPacket(s.getBytes(), s.getBytes().length, IPAddress, 8000);
						clientSocket.send(sendPacket);
						Thread.sleep(100);
					}
				} catch (Exception e) {

				}
			}
		}.start();

		new Thread() {
			@Override
			public void run() {
				try {
					while (true) {
						String s = "1:1:1:";
						DatagramPacket sendPacket = new DatagramPacket(s.getBytes(), s.getBytes().length, IPAddress, 8000);
						clientSocket.send(sendPacket);
						Thread.sleep(100);
					}
				} catch (Exception e) {

				}
			}
		}.start();

	}
}
