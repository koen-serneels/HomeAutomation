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

