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
package be.error.rpi.knx;

import static tuwien.auto.calimero.link.KNXNetworkLinkIP.TUNNELING;
import static tuwien.auto.calimero.link.medium.TPSettings.TP1;
import static tuwien.auto.calimero.log.LogLevel.WARN;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.CloseEvent;
import tuwien.auto.calimero.FrameEvent;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.NetworkLinkListener;
import tuwien.auto.calimero.log.LogLevel;
import tuwien.auto.calimero.log.LogManager;
import tuwien.auto.calimero.log.LogWriter;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;
import tuwien.auto.calimero.process.ProcessListener;

/**
 * @author Koen Serneels
 */
public class KnxConnectionFactory {

	private static final Logger logger = LoggerFactory.getLogger(KnxConnectionFactory.class);

	private static volatile KnxConnectionFactory knxConnectionFactory;

	private KNXNetworkLinkIP knxNetworkLinkIP;
	private final String knxIp;
	private final int knxPort;
	private final String localIp;

	private List<ProcessListener> processListeners = new ArrayList<>();
	private volatile ProcessCommunicator processCommunicator;

	private KnxConnectionFactory(final String knxIp, final int knxPort, final String localIp) {
		this.knxIp = knxIp;
		this.knxPort = knxPort;
		this.localIp = localIp;
		buildLink();
	}

	public static KnxConnectionFactory initialize(final String knxIp, final int knxPort, final String localIp) {
		KnxConnectionFactory knxConnectionFactory = new KnxConnectionFactory(knxIp, knxPort, localIp);
		KnxConnectionFactory.knxConnectionFactory = knxConnectionFactory;
		return knxConnectionFactory;
	}

	public static KnxConnectionFactory getInstance() {
		return knxConnectionFactory;
	}

	public void addProcessListener(ProcessListener processListener) {
		this.processCommunicator.addProcessListener(processListener);
		this.processListeners.add(processListener);
	}

	public void runWithProcessCommunicator(RunWithProcessCommunicator runWithProcessCommunicator) throws Exception{
		ProcessCommunicator pc = null;

		try {
			pc = createProcessCommunicator();
			runWithProcessCommunicator.run(pc);
		} catch (Exception exception) {
			logger.error("runWithProcessCommunicator encountered exception", exception);
			throw exception;
		} finally {
			if (pc != null) {
				pc.detach();
			}
		}
	}

	private synchronized void buildLink() {
		if (knxNetworkLinkIP != null && knxNetworkLinkIP.isOpen()) {
			logger.error("Trying to build link, but link already open");
			return;
		}

		try {
			LogManager.getManager().addWriter(null, new LogWriter() {
				@Override
				public void write(final String logService, final LogLevel level, final String msg) {
					if (!level.higher(WARN)) {
						logger.debug(("Level:" + level + " Message:" + msg));
					}
				}

				@Override
				public void write(final String logService, final LogLevel level, final String msg, final Throwable t) {
					if (!level.higher(WARN)) {
						logger.debug(("Level:" + level + " Message:" + msg), t);
					}
				}

				@Override
				public void flush() {

				}

				@Override
				public void close() {

				}
			});
			knxNetworkLinkIP = new KNXNetworkLinkIP(TUNNELING, new InetSocketAddress(localIp, 0), new InetSocketAddress(knxIp, knxPort), true, TP1);

			knxNetworkLinkIP.addLinkListener(new NetworkLinkListener() {
				@Override
				public void confirmation(final FrameEvent e) {

				}

				@Override
				public void indication(final FrameEvent e) {

				}

				@Override
				public void linkClosed(final CloseEvent e) {
					try {
						knxNetworkLinkIP.close();
					} catch (RuntimeException r) {
						//Don't care
					}

					try {
						processCommunicator.detach();
					} catch (RuntimeException r) {
						//Don't care
					}

					try {
						Thread.sleep(10000);
					} catch (InterruptedException i) {
						logger.error("COULD NOT WAIT BEFORE RESTARTING LINK", i);
					}

					logger.error("Link closed. Initiator:" + e.getInitiator() + " Reason:" + e.getReason() + " " + (e.getSource() != null ?
							("Source:" + e.getSource().toString() + " " + "Source class:" + e.getSource().getClass().getName()) :
							"Source:null"));

					logger.error("RESTARTING KNX LINK - RED LEADER STANDING BY");
					buildLink();
				}
			});

			while (!knxNetworkLinkIP.isOpen()) {

			}
			this.processCommunicator = createProcessCommunicator();
			this.processListeners.forEach(p -> this.processCommunicator.addProcessListener(p));
		} catch (Exception e) {
			logger.error("Could not create KNXNetworkLink", e);
			throw new RuntimeException(e);
		}
	}

	private ProcessCommunicator createProcessCommunicator() {
		try {
			return new ProcessCommunicatorImpl(knxNetworkLinkIP);
		} catch (KNXLinkClosedException knxLinkClosedExcepon) {
			try {
				logger.error("Could not create processCommunicator, waiting 5 secs", knxLinkClosedExcepon);
				Thread.sleep(5000);
				return createProcessCommunicator();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return createProcessCommunicator();
			}
		}
	}

	public interface RunWithProcessCommunicator {

		void run(ProcessCommunicator pc) throws Exception;
	}
}
