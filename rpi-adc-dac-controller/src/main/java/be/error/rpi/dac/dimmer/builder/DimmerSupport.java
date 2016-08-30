package be.error.rpi.dac.dimmer.builder;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.exception.KNXException;

/**
 * @author Koen Serneels
 */
public class DimmerSupport {

	private static final Logger logger = LoggerFactory.getLogger(DimmerSupport.class);

	public static GroupAddress createGroupAddress(String groupAddress) {
		try {
			return new GroupAddress(groupAddress);
		} catch (KNXException knxException) {
			logger.error("Could not create group address from " + groupAddress, knxException);
			throw new RuntimeException(knxException);
		}
	}

	public static byte[] convertDimPercentageToDacBytes(BigDecimal bigDecimal) {
		BigDecimal result = new BigDecimal(1023).divide(new BigDecimal(100)).multiply(bigDecimal).setScale(0, RoundingMode.HALF_UP);
		byte[] b = result.toBigInteger().toByteArray();
		if (b.length == 1) {
			return new byte[] { 0, b[0] };
		}
		return b;
	}
}
