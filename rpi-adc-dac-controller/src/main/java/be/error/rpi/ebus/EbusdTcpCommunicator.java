package be.error.rpi.ebus;

import static be.error.rpi.config.RunConfig.getInstance;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EbusdTcpCommunicator {

	private static final Logger logger = LoggerFactory.getLogger("temp");

	public void send(EbusCommand... ebusCommands) throws Exception {
		try (Socket clientSocket = new Socket(getInstance().getEbusdIp(), getInstance().getEbusdPort())) {
			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			for (EbusCommand ebusCommand : ebusCommands) {
				for (String command : ebusCommand.getCommands()) {
					logger.debug("Writing to ebus: hex " + command);
					out.writeBytes("hex " + command + "\n");
					String result = in.readLine();
					if (!result.equals("00")) {
						logger.error("Command hex " + command + " resulted in " + result + " should  have been 00");
					}
					in.readLine();
					Thread.sleep(200);
				}
				out.close();
				in.close();
			}
		}
	}
}
