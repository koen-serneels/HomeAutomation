package be.error.rpi.knx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.exception.KNXException;

public class Support {

	private static final Logger logger = LoggerFactory.getLogger(Support.class);

	public static GroupAddress createGroupAddress(String groupAddress) {
		try {
			return new GroupAddress(groupAddress);
		} catch (KNXException knxException) {
			logger.error("Could not create group address from " + groupAddress, knxException);
			throw new RuntimeException(knxException);
		}
	}
}
