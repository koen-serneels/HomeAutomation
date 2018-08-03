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
package be.error.rpi;

import static be.error.rpi.config.RunConfig.getInstance;
import static be.error.rpi.config.RunConfig.initialize;
import static be.error.rpi.heating.EbusDeviceAddress.GROUND_FLOOR;
import static be.error.rpi.knx.Support.createGroupAddress;
import static be.error.types.LocationId.GELIJKVLOERS;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.GroupAddress;

import be.error.rpi.adc.AdcController;
import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.config.dimmers.buiten.DimmerVoordeur;
import be.error.rpi.dac.dimmer.config.dimmers.gv.DimmerBerging;
import be.error.rpi.dac.dimmer.config.dimmers.gv.DimmerEethoek;
import be.error.rpi.dac.dimmer.config.dimmers.gv.DimmerGang;
import be.error.rpi.dac.dimmer.config.dimmers.gv.DimmerGarage;
import be.error.rpi.dac.dimmer.config.dimmers.gv.DimmerInkomhal;
import be.error.rpi.dac.dimmer.config.dimmers.gv.DimmerKeuken;
import be.error.rpi.dac.dimmer.config.dimmers.gv.DimmerWc;
import be.error.rpi.dac.dimmer.config.dimmers.gv.DimmerZitHoek;
import be.error.rpi.dac.dimmer.config.scenes.Gang;
import be.error.rpi.dac.dimmer.config.scenes.GvComfort;
import be.error.rpi.ebus.EbusCommand;
import be.error.rpi.ebus.commands.GetDepartWaterTemperatureGv;
import be.error.rpi.ebus.commands.GetHeatingCircuitEnabled;
import be.error.rpi.ebus.commands.GetHeatingCircuitHeatingDemandGv;
import be.error.rpi.ebus.commands.GetOutsideTemperature;
import be.error.rpi.heating.HeatingController;
import be.error.rpi.heating.HeatingInfoPollerJobSchedulerFactory;
import be.error.rpi.sonos.SonosController;

/**
 * @author Koen Serneels
 */
public class StartRpiGv {

	private static final Logger logger = LoggerFactory.getLogger(StartRpiGv.class);

	private static final String RPI_LAN_IP = "192.168.0.10";

	public static void main(String[] args) throws Exception {
		initialize(RPI_LAN_IP);
		getInstance().registerLucidControlAO4(0, "/dev/lucidAO0");

		Map<EbusCommand<?>, GroupAddress> config = new HashMap<>();
		config.put(new GetHeatingCircuitHeatingDemandGv(), createGroupAddress("10/2/1"));
		config.put(new GetHeatingCircuitEnabled(), createGroupAddress("10/2/2"));
		config.put(new GetOutsideTemperature(), createGroupAddress("10/0/16"));
		config.put(new GetDepartWaterTemperatureGv(), createGroupAddress("10/2/4"));
		HeatingInfoPollerJobSchedulerFactory heatingInfoPollerJobSchedulerFactory = new HeatingInfoPollerJobSchedulerFactory(GROUND_FLOOR, config);

		new Thread(() -> {
			while (true) {
				try {
					AdcController adcController = new AdcController();
					adcController.run();
				} catch (Exception e) {
					logger.error("ADC CONTROLLER DID NOT START", e);
				}
			}
		}).start();

		new Thread(() -> {
			try {
				new DimmerVoordeur().start();
				new DimmerWc().start();
				new DimmerBerging().start();
				new DimmerGarage().start();

				Dimmer dimmerEethoek = new DimmerEethoek().start();
				Dimmer dimmerInkomhal = new DimmerInkomhal().start();
				Dimmer dimmerKeuken = new DimmerKeuken().start();
				Dimmer dimmerZithoek = new DimmerZitHoek().start();
				Dimmer dimmerGang = new DimmerGang().start();

				new Gang(dimmerInkomhal, dimmerGang).run();
				new GvComfort(dimmerEethoek, dimmerZithoek, dimmerKeuken).run();
			} catch (Exception e) {
				logger.error("DAC CONTROLLER DID NOT START", e);
			}
		}).start();

		new Thread(() -> {
			try {
				HeatingController heatingController = new HeatingController(GROUND_FLOOR, heatingInfoPollerJobSchedulerFactory);
				heatingController.registerRoom(GELIJKVLOERS, createGroupAddress("10/2/0"));
				heatingController.start();
			} catch (Exception e) {
				logger.error("HEATING CONTROLLER DID NOT START", e);
			}
		}).start();

		new SonosController().start();

		logger.debug("Done starting up threads");
	}
}


