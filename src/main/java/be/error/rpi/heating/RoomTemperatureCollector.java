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
package be.error.rpi.heating;

import static be.error.rpi.config.RunConfig.getInstance;
import static be.error.rpi.knx.UdpChannelCommand.Constants.TEMPERATURE;
import static be.error.rpi.knx.UdpChannelCommand.fromString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.DetachEvent;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.process.ProcessEvent;

import be.error.rpi.dac.dimmer.builder.AbstractDimmerProcessListener;
import be.error.rpi.knx.UdpChannel.UdpChannelCallback;
import be.error.rpi.knx.UdpChannelCommand;
import be.error.types.LocationId;

public class RoomTemperatureCollector {

	private static final Logger logger = LoggerFactory.getLogger("heating");

	private final LocationId roomId;
	private final HeatingController heatingController;

	private final GroupAddress currentTemperatureGa;
	private final UdpChannelCommand desiredTempCommand;

	private final ControlValueCalculator controlValueCalculator = new ControlValueCalculator();

	private final RoomTemperature roomTemperature;

	public RoomTemperatureCollector(final LocationId roomId, final HeatingController heatingController, GroupAddress currentTemperatureGa) {
		this.roomId = roomId;
		this.heatingController = heatingController;
		this.currentTemperatureGa = currentTemperatureGa;
		this.desiredTempCommand = fromString(TEMPERATURE + "_" + roomId);
		this.roomTemperature = new RoomTemperature(roomId);
	}

	public void start() {
		getInstance().addUdpChannelCallback(new UdpChannelCallback() {
			@Override
			public UdpChannelCommand command() {
				return desiredTempCommand;
			}

			@Override
			public void callBack(final String desiredTemp) throws Exception {
				logger.debug("Receiving desired temperature " + desiredTemp + "for room " + roomId);
				synchronized (roomTemperature) {
					roomTemperature.updateDesiredTemp(desiredTemp);
					processTemperatureChange();
				}
			}
		});

		getInstance().getKnxConnectionFactory().addProcessListener(new AbstractDimmerProcessListener() {
			@Override
			public void groupWrite(final ProcessEvent e) {
				if (e.getDestination().equals(currentTemperatureGa)) {
					try {
						Double currentTemp = super.asFloat(e, false);
						logger.debug("Receiving current temperature " + currentTemp + "for room " + roomId);
						synchronized (roomTemperature) {
							roomTemperature.updateCurrentTemp(currentTemp);
							processTemperatureChange();
						}
					} catch (Exception exception) {
						logger.error("Error converting received desired temperature value from KNX", exception);
					}
				}
			}

			@Override
			public void groupReadRequest(final ProcessEvent e) {

			}

			@Override
			public void groupReadResponse(final ProcessEvent e) {

			}

			@Override
			public void detached(final DetachEvent e) {

			}
		});
		logger.debug(RoomTemperatureCollector.class.getName() + " started for room:" + roomId);
	}

	private void processTemperatureChange() {
		if (!roomTemperature.ready()) {
			logger.debug("Room temperature for room " + roomTemperature.getRoomId() + " not ready. Values:" + roomTemperature);
			return;
		}

		controlValueCalculator.updateHeatingDemand(roomTemperature);
		logger.debug("Updated heating demand for room " + roomTemperature.getRoomId() + " new value:" + roomTemperature.getHeatingDemand());

		RoomTemperature toSend = roomTemperature.clone();
		logger.debug("Sending room temp info: " + toSend);
		heatingController.send(toSend);
	}
}
