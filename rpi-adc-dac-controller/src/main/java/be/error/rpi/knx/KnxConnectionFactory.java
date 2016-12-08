package be.error.rpi.knx;

import static tuwien.auto.calimero.link.KNXNetworkLinkIP.TUNNELING;
import static tuwien.auto.calimero.log.LogLevel.WARN;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.CloseEvent;
import tuwien.auto.calimero.FrameEvent;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.NetworkLinkListener;
import tuwien.auto.calimero.link.medium.TPSettings;
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

	private KNXNetworkLinkIP knxNetworkLinkIP;

	public KnxConnectionFactory(final String knxIp, final int knxPort, final String localIp) {
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
			knxNetworkLinkIP = new KNXNetworkLinkIP(TUNNELING, new InetSocketAddress(localIp, 0), new InetSocketAddress(knxIp, knxPort), true, new TPSettings(true));

			knxNetworkLinkIP.addLinkListener(new NetworkLinkListener() {
				@Override
				public void confirmation(final FrameEvent e) {

				}

				@Override
				public void indication(final FrameEvent e) {

				}

				@Override
				public void linkClosed(final CloseEvent e) {
					logger.error("Link closed. Initiator:" + e.getInitiator() + " Reason:" + e.getReason() + " " + (e.getSource() != null ?
							("Source:" + e.getSource().toString() + " " + "Source class:" + e.getSource().getClass().getName()) :
							"Source:null"));
				}
			});
		} catch (Exception e) {
			logger.error("Could not create KNXNetworkLink", e);
			throw new RuntimeException(e);
		}
	}

	public ProcessCommunicator createProcessCommunicator(ProcessListener... processListeners) {
		try {

			ProcessCommunicatorImpl pc = new ProcessCommunicatorImpl(knxNetworkLinkIP);

			if (processListeners != null) {
				for (ProcessListener processListener : processListeners) {
					pc.addProcessListener(processListener);
				}
			}
			return pc;
		} catch (Exception e) {
			logger.error("Could not create KNX ProcessCommunicator", e);
			throw new RuntimeException(e);
		}
	}
}
