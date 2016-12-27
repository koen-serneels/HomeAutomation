package be.error.rpi.knx;

import static be.error.rpi.config.RunConfig.LOCAL_IP;
import static be.error.rpi.knx.UdpChannelCommand.fromString;
import static java.net.InetAddress.getByName;
import static org.apache.commons.lang3.ArrayUtils.remove;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UdpChannel extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(UdpChannel.class);

	private final List<UdpChannelCallback> udpChannelCallbacks = new ArrayList<>();
	private final DatagramSocket clientSocket;

	public UdpChannel(int port, UdpChannelCallback... udpChannelCallbacks) throws Exception {
		addUdpChannelCallback(udpChannelCallbacks);
		clientSocket = new DatagramSocket(port, getByName(LOCAL_IP));
	}

	public void run() {
		String s = null;
		try {
			while (true) {
				byte b[] = new byte[256];
				DatagramPacket receivePacket = new DatagramPacket(b, b.length);
				clientSocket.receive(receivePacket);
				s = new String(b, "UTF8").trim();
				String[] split = StringUtils.split(s, "|");
				UdpChannelCommand udpChannelCommand = fromString(split[0]);
				this.udpChannelCallbacks.stream().filter(cb -> cb.isApplicable(udpChannelCommand)).forEach(cb -> {
					try {
						cb.callBack(StringUtils.join(remove(split, 0)));
					} catch (Exception e) {
						logger.error("UdpChannel on port " + clientSocket.getPort() + " with command " + cb.command().toString(), e);
					}
				});
			}
		} catch (Exception e) {
			logger.error("UdpChannel on port " + clientSocket.getPort() + " with raw command " + s, e);
		}
	}

	public void addUdpChannelCallback(UdpChannelCallback... udpChannelCallbacks) {
		if (udpChannelCallbacks != null) {
			CollectionUtils.addAll(this.udpChannelCallbacks, udpChannelCallbacks);
		}
	}

	public interface UdpChannelCallback {

		UdpChannelCommand command();

		boolean isApplicable(UdpChannelCommand udpChannelCommand);

		void callBack(String s) throws Exception;
	}
}
