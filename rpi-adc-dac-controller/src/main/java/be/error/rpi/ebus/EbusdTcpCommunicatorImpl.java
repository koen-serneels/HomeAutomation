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

/**
 * Created by koen on 04.01.17.
 */
public class EbusdTcpCommunicatorImpl implements EbusdTcpCommunicator {
	private static final Logger logger = LoggerFactory.getLogger("ebusd");

	public List<String> send(EbusCommand ebusCommand) throws Exception {
		try (Socket clientSocket = new Socket(getInstance().getEbusdIp(), getInstance().getEbusdPort())) {
			List<String> results = new ArrayList<>();
			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			for (String command : ebusCommand.getEbusCommands()) {
				logger.debug("Writing to ebus: hex " + command);
				out.writeBytes("hex " + command + "\n");
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

