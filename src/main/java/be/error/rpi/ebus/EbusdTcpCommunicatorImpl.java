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
package be.error.rpi.ebus;

import static be.error.rpi.config.RunConfig.getInstance;
import static java.lang.Thread.sleep;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.error.rpi.heating.EbusDeviceAddress;

public class EbusdTcpCommunicatorImpl implements EbusdTcpCommunicator {
	private static final Logger logger = LoggerFactory.getLogger("ebusd");
	private final EbusDeviceAddress ebusDeviceAddress;

	public EbusdTcpCommunicatorImpl(final EbusDeviceAddress ebusDeviceAddress) {
		this.ebusDeviceAddress = ebusDeviceAddress;
	}

	public List<String> send(EbusCommand ebusCommand) throws Exception {
		try (Socket clientSocket = new Socket(getInstance().getEbusdIp(), getInstance().getEbusdPort())) {
			List<String> results = new ArrayList<>();
			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			for (String command : ebusCommand.getEbusCommands()) {
				String toSend = "hex -s " + ebusDeviceAddress.getEbusAddressPrefix() + " " + command;
				logger.debug("Writing to ebus: " + toSend);
				out.writeBytes(toSend + "\n");
				String result = in.readLine();
				logger.debug("  Result:" + result);
				if (!ebusCommand.withResult() && !result.equals("00")) {
					logger.error("Command hex " + command + " resulted in " + result + " should  have been 00");
				}
				results.add(result);
				in.readLine();
				sleep(200);
			}
			out.close();
			in.close();
			return results;
		}
	}
}

