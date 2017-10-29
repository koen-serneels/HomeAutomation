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
