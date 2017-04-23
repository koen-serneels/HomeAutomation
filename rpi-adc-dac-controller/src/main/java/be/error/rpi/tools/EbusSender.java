package be.error.rpi.tools;

import static java.lang.Thread.sleep;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import be.error.rpi.ebus.EbusCommand;
import be.error.rpi.ebus.commands.SetCurrentRoomTemperature;
import be.error.rpi.ebus.commands.SetDesiredRoomTemperature;
import be.error.rpi.heating.RoomTemperature;
import be.error.types.LocationId;

public class EbusSender {

	public static void main(String[] args) throws Exception {

		//SetDesiredRoomTemperature desiredRoomTemperature = new SetDesiredRoomTemperature(new BigDecimal("23.00"));
		//SetCurrentRoomTemperature setCurrentRoomTemperature = new SetCurrentRoomTemperature(new BigDecimal("23.00"), new BigDecimal("23.00"));
		//send(desiredRoomTemperature);
		//send(setCurrentRoomTemperature);
	}

	public static List<String> send(EbusCommand ebusCommand) throws Exception {
		try (Socket clientSocket = new Socket("192.168.0.10", 8888)) {
			List<String> results = new ArrayList<>();
			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			for (String command : ebusCommand.getEbusCommands()) {
				out.writeBytes("hex " + command + "\n");
				String result = in.readLine();
				if (!ebusCommand.withResult() && !result.equals("00")) {
					throw new IllegalStateException();
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
