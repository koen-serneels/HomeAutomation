package be.error.rpi.ebus;

import java.util.List;

public interface EbusCommand<R> {

	String[] getEbusCommands();

	R convertResult(List<String> results);

	default boolean withResult() {
		return false;
	}
}
