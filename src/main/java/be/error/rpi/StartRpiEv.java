package be.error.rpi;

import static be.error.rpi.config.RunConfig.getInstance;
import static be.error.rpi.config.RunConfig.initialize;
import static be.error.rpi.heating.EbusDeviceAddress.FIRST_FLOOR;
import static be.error.rpi.knx.Support.createGroupAddress;
import static be.error.types.LocationId.BADKAMER;
import static be.error.types.LocationId.SK1;
import static be.error.types.LocationId.SK2;
import static be.error.types.LocationId.SK3;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.GroupAddress;

import be.error.rpi.dac.dimmer.config.dimmers.buiten.DimmerAg;
import be.error.rpi.dac.dimmer.config.dimmers.buiten.DimmerLzg;
import be.error.rpi.dac.dimmer.config.dimmers.buiten.DimmerRzg;
import be.error.rpi.dac.dimmer.config.dimmers.buiten.DimmerVg;
import be.error.rpi.dac.dimmer.config.dimmers.ev.DimmerBadkamer;
import be.error.rpi.dac.dimmer.config.dimmers.ev.DimmerDressing;
import be.error.rpi.dac.dimmer.config.dimmers.ev.DimmerNachthal;
import be.error.rpi.dac.dimmer.config.dimmers.ev.DimmerSk1;
import be.error.rpi.dac.ventilation.VentilationUdpCallback;
import be.error.rpi.ebus.EbusCommand;
import be.error.rpi.ebus.commands.GetDepartWaterTemperatureEv;
import be.error.rpi.ebus.commands.GetHeatingCircuitEnabled;
import be.error.rpi.ebus.commands.GetHeatingCircuitHeatingDemandEv;
import be.error.rpi.heating.HeatingController;
import be.error.rpi.heating.HeatingInfoPollerJobSchedulerFactory;

/**
 * @author Koen Serneels
 */
public class StartRpiEv {

	private static final Logger logger = LoggerFactory.getLogger(StartRpiEv.class);

	private static final String RPI_LAN_IP = "192.168.0.11";

	public static void main(String[] args) throws Exception {
		initialize(RPI_LAN_IP);

		Map<EbusCommand<?>, GroupAddress> config = new HashMap<>();
		config.put(new GetHeatingCircuitHeatingDemandEv(), createGroupAddress("10/3/0"));
		config.put(new GetHeatingCircuitEnabled(), createGroupAddress("10/3/1"));
		config.put(new GetDepartWaterTemperatureEv(), createGroupAddress("10/3/2"));
		HeatingInfoPollerJobSchedulerFactory heatingInfoPollerJobSchedulerFactory = new HeatingInfoPollerJobSchedulerFactory(FIRST_FLOOR, config);

		new Thread(() -> {
			try {
				new DimmerBadkamer().start();
				new DimmerDressing().start();
				new DimmerNachthal().start();
				new DimmerSk1().start();
				new DimmerAg().start();
				new DimmerLzg().start();
				new DimmerRzg().start();
				new DimmerVg().start();

				getInstance().addUdpChannelCallback(new VentilationUdpCallback());
			} catch (Exception e) {
				logger.error("DAC CONTROLLER DID NOT START", e);
			}
		}).start();

		new Thread(() -> {
			try {
				HeatingController heatingController = new HeatingController(FIRST_FLOOR, heatingInfoPollerJobSchedulerFactory);
				heatingController.registerRoom(BADKAMER, createGroupAddress("10/0/4"), createGroupAddress("13/0/0"), createGroupAddress("13/1/0"));
				heatingController.registerRoom(SK1, createGroupAddress("10/0/0"), createGroupAddress("13/2/0"));
				heatingController.registerRoom(SK2, createGroupAddress("10/0/1"), createGroupAddress("13/3/0"));
				heatingController.registerRoom(SK3, createGroupAddress("10/0/2"), createGroupAddress("13/4/0"));
				heatingController.start();
			} catch (Exception e) {
				logger.error("HEATING CONTROLLER DID NOT START", e);
			}
		}).start();

		logger.debug("Done starting up threads");
	}
}



