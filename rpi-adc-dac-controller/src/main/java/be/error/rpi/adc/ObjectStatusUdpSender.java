package be.error.rpi.adc;

import static org.apache.commons.lang3.ArrayUtils.add;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import be.error.rpi.adc.ObjectStatusTypeMapper.ObjectStatusType;

/**
 * @author Koen Serneels
 */
public class ObjectStatusUdpSender {

	private String host;
	private int port;

	private Map<String, ObjectStatusType> mappers = new HashMap<>();

	private final InetAddress IPAddress;
	private final DatagramSocket clientSocket;

	public ObjectStatusUdpSender(final String host, int port) throws Exception {
		this.host = host;
		this.port = port;
		this.IPAddress = InetAddress.getByName(host);
		this.clientSocket = new DatagramSocket();
	}

	public void send(List<Pair<AdcChannel, ObjectStatusType>> results) throws Exception {
		for (Pair<AdcChannel, ObjectStatusType> pair : results) {

			byte id = (byte) Integer.parseInt(pair.getLeft().getId());
			byte[] toSend = add(new byte[] { id }, pair.getRight().getId());
			DatagramPacket sendPacket = new DatagramPacket(toSend, toSend.length, IPAddress, port);
			clientSocket.send(sendPacket);
		}
	}
}

