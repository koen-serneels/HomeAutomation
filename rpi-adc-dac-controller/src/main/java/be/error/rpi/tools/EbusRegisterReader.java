package be.error.rpi.tools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;

public class EbusRegisterReader {

	public static void main(String[] args) throws Exception {
		InputStream fis = new FileInputStream("/home/koen/devel/projects/HomeAutomation/commands-ev");
		InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		BufferedReader br = new BufferedReader(isr);

		try (Socket clientSocket = new Socket("192.168.0.10", 8888)) {
			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			String s = null;
			while ((s = br.readLine()) != null) {
				out.writeBytes(s + "\n");
				String result = in.readLine();
				System.err.println(s + " - " + result);
				in.readLine();
			}
		}
	}
/*
	//String template = "15b5090329%s00";
	String template = "  15b509030d%s%s";

	InputStream fis = new FileInputStream("/home/koen/devel/projects/HomeAutomation/commands");
	InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
	BufferedReader br = new BufferedReader(isr);

		try (Socket clientSocket = new Socket("192.168.0.10", 8888)) {
		DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		for (int x = 0; x <= 1; x++) {
			for (int i = 0; i < 255; i++) {
				String cmd = String.format(template, leftPad(new BigInteger("" + i).toString(16), 2, "0"), leftPad(new BigInteger("" + x).toString(16), 2, "0"));
				//String c = "hex -s " + EbusDeviceAddress.GROUND_FLOOR.getEbusAddressPrefix() + " " + cmd + "\n";
				out.writeBytes(br.readLine());
				String result = in.readLine();
				System.err.println(cmd + " - " + result);
				if (!result.contains("ERR")) {
						fileWriter.write(c);
						fileWriter.flush();
					}
				in.readLine();
			}
		}
		out.close();
		in.close();
	}
*/
}

