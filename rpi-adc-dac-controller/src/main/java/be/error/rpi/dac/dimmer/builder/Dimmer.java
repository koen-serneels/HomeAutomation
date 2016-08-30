package be.error.rpi.dac.dimmer.builder;

import static be.error.rpi.config.RunConfig.getInstance;
import static be.error.rpi.dac.dimmer.builder.DimDirection.DOWN;
import static be.error.rpi.dac.dimmer.builder.DimDirection.UP;
import static tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned.DPT_PERCENT_U8;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned;
import tuwien.auto.calimero.process.ProcessCommunicator;

import be.error.rpi.dac.dimmer.config.DimmerName;

/**
 * @author Koen Serneels
 * @see DimmerBuilder for more information on configuring a {@link Dimmer}
 */
public class Dimmer extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(Dimmer.class);
	private static final BigDecimal ZERO = new BigDecimal("0.0");

	private long turnOffDelay = 2000;
	private long delayBeforeIncreasingDimValue = 300;
	private int stepDelay = 30;

	private List<GroupAddress> feedbackGroupAddresses = new ArrayList();
	private List<GroupAddress> switchGroupAddresses = new ArrayList();

	private BigDecimal curVal = ZERO;
	private BigDecimal sentVal = ZERO;
	private BigDecimal minDimValue = new BigDecimal("1.0");
	private DimDirection lastDimDirection;

	private final AtomicBoolean interupt = new AtomicBoolean(false);
	private final BlockingQueue<DimmerCommand> commandQueue = new LinkedBlockingDeque();

	private final DimmerName dimmerName;
	private final int boardAddress;
	private final int channel;

	private final ProcessCommunicator pc;

	public Dimmer(DimmerName dimmerName, int boardAddress, int channel, List<GroupAddress> switchGroupAddresses, List<GroupAddress> feedbackGroupAddresses)
			throws IOException {

		this.dimmerName = dimmerName;
		this.boardAddress = boardAddress;
		this.channel = channel;
		this.switchGroupAddresses = switchGroupAddresses;
		this.feedbackGroupAddresses = feedbackGroupAddresses;
		this.pc = getInstance().getKnxConnectionFactory().createProcessCommunicator();
	}

	public void run() {

		while (true) {
			try {
				DimmerCommand dimmerCommand = commandQueue.take();
				interupt.set(false);

				if (curVal.equals(ZERO) && dimmerCommand.getTargetVal().compareTo(ZERO) > 0) {
					for (GroupAddress groupAddress : switchGroupAddresses) {
						pc.write(groupAddress, true);
					}
					sleep(delayBeforeIncreasingDimValue);
				}

				if (dimmerCommand.getTargetVal().compareTo(curVal) > 0) {
					lastDimDirection = UP;
				} else if (dimmerCommand.getTargetVal().compareTo(curVal) < 0) {
					lastDimDirection = DOWN;
				}

				while (curVal.compareTo(dimmerCommand.getTargetVal()) != 0) {
					processCommand(dimmerCommand);
					if (commandQueue.peek() != null) {
						break;
					}
					if (interupt.get()) {
						break;
					}
					sleep(stepDelay);
					if (interupt.get()) {
						break;
					}
				}

				//TODO KSE Add something configurable
				if (!dimmerCommand.getOrigin().equals(getInstance().getLoxoneIa())) {
					for (GroupAddress groupAddress : feedbackGroupAddresses) {
						DPTXlator8BitUnsigned dDPTXlator8BitUnsigned = new DPTXlator8BitUnsigned(DPT_PERCENT_U8);
						dDPTXlator8BitUnsigned.setValue(getCurVal().intValue());
						pc.write(groupAddress, dDPTXlator8BitUnsigned);
					}
				}

				if (curVal.compareTo(ZERO) == 0) {
					for (int i = 0; i < turnOffDelay; i++) {
						sleep(1);
						if (interupt.get()) {
							break;
						}
					}

					if (!interupt.get()) {
						for (GroupAddress groupAddress : switchGroupAddresses) {
							try {
								pc.write(groupAddress, false);
							} catch (Exception e) {
								logger.error("Dimmer " + dimmerName + " turnoff delay threed got exception while sending to " + groupAddress.toString(), e);
							}
						}
					}
				}
			} catch (InterruptedException e) {
				logger.error("Dimmer " + dimmerName + " got interrupt", e);
				interrupt();
				//Do nothing
			} catch (Exception e) {
				logger.error("Dimmer " + dimmerName + " got exception", e);
				interrupt();
			}
		}
	}

	public synchronized void putCommand(DimmerCommand dimmerCommand) throws Exception {
		commandQueue.put(dimmerCommand);
	}

	public void interrupt() {
		interupt.set(true);
	}

	private void processCommand(DimmerCommand dimmerCommand) throws IOException {
		if (curVal.compareTo(dimmerCommand.getTargetVal()) < 0) {
			curVal = curVal.add(new BigDecimal("1.0"));
		} else {
			curVal = curVal.subtract(new BigDecimal("1.0"));
		}

		//If curval is below  mindimval we sent mindimval instead. This ensures that the driver does not turn itself of if the dim value gets too low. The driver
		// should be turned off via it's relay (~KNX actor) instead and not by the dimmer input. Curval remains the internal state as it is also used to determine when
		// the relais is to be switched off (namely when curval reaches ZERO). The value actually sent to the DAC is then reflected as sentval (mostly for debug
		// purposes)
		sentVal = curVal.compareTo(minDimValue) < 0 ? minDimValue : curVal;

		dim(sentVal);
	}

	private void dim(BigDecimal targetValue) throws IOException {
		byte[] b = DimmerSupport.convertDimPercentageToDacBytes(targetValue);
		getInstance().getI2CCommunicator().write(boardAddress, channel, new byte[] { (byte) channel, b[1], b[0] });
	}

	public BigDecimal getCurVal() {
		return curVal;
	}

	public DimDirection getLastDimDirection() {
		return lastDimDirection;
	}

	void setTurnOffDelay(final long turnOffDelay) {
		this.turnOffDelay = turnOffDelay;
	}

	void setDelayBeforeIncreasingDimValue(final long delayBeforeIncreasingDimValue) {
		this.delayBeforeIncreasingDimValue = delayBeforeIncreasingDimValue;
	}

	void setStepDelay(final int stepDelay) {
		this.stepDelay = stepDelay;
	}
}

