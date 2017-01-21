package be.error.rpi.dac.ventilation;

import static be.error.rpi.config.RunConfig.getInstance;
import static be.error.rpi.dac.support.Support.convertPercentageToDacBytes;
import static be.error.rpi.knx.UdpChannelCommand.VENTILATIE;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.error.rpi.knx.UdpChannel.UdpChannelCallback;
import be.error.rpi.knx.UdpChannelCommand;

public class VentilationUdpCallback implements UdpChannelCallback {

	private static final Logger logger = LoggerFactory.getLogger("vent");

	private final BigDecimal MIN_PCT = new BigDecimal("10");

	private int boardAddress = 0x58;
	private int channel = 1;

	@Override
	public boolean isApplicable(final UdpChannelCommand udpChannelCommand) {
		return udpChannelCommand == VENTILATIE;
	}

	@Override
	public UdpChannelCommand command() {
		return VENTILATIE;
	}

	@Override
	public void callBack(final String s) throws Exception {
		BigDecimal val = new BigDecimal(s);
		if (val.compareTo(MIN_PCT) < 0) {
			val = MIN_PCT;
		}
		getInstance().getI2CCommunicator().write(boardAddress, channel, convertPercentageToDacBytes(val));
		logger.debug(s + " (send:" + val + ")");
	}
}
