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
package be.error.rpi.config;

import static com.pi4j.io.i2c.I2CBus.BUS_1;
import static java.lang.System.setProperty;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;

import lucidio.LucidControlAO4;
import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.exception.KNXException;

import be.error.rpi.adc.ObjectStatusTypeMapper.ObjectStatusType;
import be.error.rpi.dac.i2c.I2CCommunicator;
import be.error.rpi.knx.KnxConnectionFactory;
import be.error.rpi.knx.UdpChannel;
import be.error.rpi.knx.UdpChannel.UdpChannelCallback;

/**
 * @author Koen Serneels
 */
public class RunConfig {

	private static final Logger logger = LoggerFactory.getLogger(RunConfig.class);

	public static String LOCAL_IP;

	private final String LOXONE_IP = "192.168.0.5";
	private final IndividualAddress LOXONE_IA;
	private final int LOXONE_PORT = 6000;

	private final String KNX_IP = "192.168.0.6";
	private final int KNX_PORT = 3671;

	private final String EBUSD_IP = "192.168.0.10";
	private final int EBUSD_PORT = 8888;

	private final int UDP_CHAN_PORT = 8010;

	private final Scheduler scheduler;

	private I2CBus bus;
	private I2CCommunicator i2CCommunicator;
	private UdpChannel udpChannel;

	private Map<Integer, LucidControlAO4> lucidControlMap = new HashMap<>();

	private EventBus adcEventBus = new EventBus();

	private KnxConnectionFactory knxConnectionFactory;

	private static RunConfig runConfig;

	private RunConfig(String localIp) {

		LOCAL_IP = localIp;
		try {
			LOXONE_IA = new IndividualAddress("1.1.250");
		} catch (KNXException knxException) {
			throw new RuntimeException(knxException);
		}

		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public static RunConfig getInstance() {
		if (runConfig == null) {
			throw new IllegalStateException("Initialize first");
		}
		return runConfig;
	}

	public static void initialize(String localIp) {
		setProperty("calimero.knxnetip.tunneling.resyncSkippedRcvSeq", "true");

		runConfig = new RunConfig(localIp);
		runConfig.initialize();
	}

	private void initialize() {
		try {
			bus = I2CFactory.getInstance(BUS_1);
			i2CCommunicator = new I2CCommunicator();
			i2CCommunicator.start();
			udpChannel = new UdpChannel(UDP_CHAN_PORT);
			udpChannel.start();
			knxConnectionFactory = KnxConnectionFactory.initialize(runConfig.getKnxIp(), runConfig.getKnxPort(), runConfig.getLocalIp());
		} catch (Exception e) {
			logger.error("Could not start I2CCommunicator", e);
			throw new RuntimeException(e);
		}
	}

	public void doWithLucidControl(Integer device, Consumer<LucidControlAO4> consumer) {
		try {
			consumer.accept(lucidControlMap.get(device));
		} catch (UncheckedIOException uncheckedIoException) {
			reInitLucidControls();
			doWithLucidControl(device, consumer);
		}
	}

	public synchronized void registerLucidControlAO4(int portId, String portName) throws IOException {
		LucidControlAO4 lucidControlAO4 = new LucidControlAO4(portName);
		lucidControlAO4.open();
		lucidControlMap.put(portId, lucidControlAO4);
	}

	private synchronized void reInitLucidControls() {
		lucidControlMap.forEach((e, v) -> {
			try {
				v.close();
			} catch (IOException ioException) {
				//Ignore
			}
			try {
				v.open();
				logger.error("Connection to Lucid device " + e + " re-established");
			} catch (IOException ioException) {
				logger.error("Could not re-open connection to Lucid device " + e + "", ioException);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException interruptedException) {
					//Do nothing
				}
				reInitLucidControls();
			}
		});
	}

	public String getLocalIp() {
		return LOCAL_IP;
	}

	public String getLoxoneIp() {
		return LOXONE_IP;
	}

	public int getLoxonePort() {
		return LOXONE_PORT;
	}

	public String getKnxIp() {
		return KNX_IP;
	}

	public int getKnxPort() {
		return KNX_PORT;
	}

	public IndividualAddress getLoxoneIa() {
		return LOXONE_IA;
	}

	public I2CBus getBus() {
		return bus;
	}

	public KnxConnectionFactory getKnxConnectionFactory() {
		return knxConnectionFactory;
	}

	public I2CCommunicator getI2CCommunicator() {
		return i2CCommunicator;
	}

	public int getEbusdPort() {
		return EBUSD_PORT;
	}

	public String getEbusdIp() {
		return EBUSD_IP;
	}

	public void registerAdcEventListener(Object o) {
		adcEventBus.register(o);
	}

	public void postAdcEvent(List<Pair<String, ObjectStatusType>> list) {
		adcEventBus.post(list);
	}

	public void addUdpChannelCallback(UdpChannelCallback... udpChannelCallbacks) {
		udpChannel.addUdpChannelCallback(udpChannelCallbacks);
	}

	public Scheduler getScheduler() {
		return scheduler;
	}
}
