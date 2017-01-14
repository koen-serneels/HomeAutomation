package be.error.rpi.ebus;

import java.util.List;

public interface EbusdTcpCommunicator {

	List<String> send(EbusCommand ebusCommand) throws Exception;
}
