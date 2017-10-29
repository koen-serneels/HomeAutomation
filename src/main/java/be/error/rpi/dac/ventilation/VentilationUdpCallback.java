/*-
 * #%L
 * Home Automation
 * %%
 * Copyright (C) 2016 - 2017 Koen Serneels
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
