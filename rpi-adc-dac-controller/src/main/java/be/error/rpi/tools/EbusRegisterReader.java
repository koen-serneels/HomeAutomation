package be.error.rpi.tools;

import static be.error.rpi.config.RunConfig.getInstance;
import static java.lang.Thread.sleep;
import static org.apache.commons.lang3.StringUtils.leftPad;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by koen on 01.07.16.
 */
public class EbusRegisterReader {

	public static void main(String[] args) throws Exception {
		String template = "15b5090329%s00";

		try (Socket clientSocket = new Socket("192.168.0.10", 8888)) {
			List<String> results = new ArrayList<>();
			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			for (int i = 0; i < 255; i++) {
				String cmd = String.format(template, leftPad(new BigInteger("" + i).toString(16), 2, "0"));
				out.writeBytes("hex " + cmd + "\n");
				String result = in.readLine();
				System.err.println(cmd + " - " + result);
				in.readLine();
				sleep(20);
			}
			out.close();
			in.close();
		}
	}
}

