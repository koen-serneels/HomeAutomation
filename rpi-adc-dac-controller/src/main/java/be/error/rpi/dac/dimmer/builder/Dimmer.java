package be.error.rpi.dac.dimmer.builder;

import static be.error.rpi.config.RunConfig.getInstance;
import static be.error.rpi.dac.dimmer.builder.DimDirection.DOWN;
import static be.error.rpi.dac.dimmer.builder.DimDirection.UP;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.commons.collections4.CollectionUtils.union;
import static tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned.DPT_PERCENT_U8;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned;
import tuwien.auto.calimero.process.ProcessCommunicator;

import be.error.types.LocationId;
import be.error.rpi.dac.support.Support;

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
	private List<GroupAddress> switchLedControlGroupAddresses = new ArrayList();
	private List<GroupAddress> switchGroupAddresses = new ArrayList();
	private List<GroupAddress> outputSwitchUpdateGroupAddresses = new ArrayList<>();

	private BigDecimal curVal = ZERO;
	private BigDecimal sentVal = ZERO;

	private BigDecimal minDimValue = new BigDecimal("1.0");
	private DimDirection lastDimDirection;

	private final AtomicBoolean interupt = new AtomicBoolean(false);
	private final BlockingQueue<DimmerCommand> commandQueue = new LinkedBlockingDeque();

	private final LocationId dimmerName;
	private final int boardAddress;
	private final int channel;

	private KnxDimmerProcessListener knxDimmerProcessListener;
	private final ProcessCommunicator pc;

	private Optional<DimmerCommand> lastDimCommand = empty();
	private Optional<DimmerCommand> activeScene = empty();

	public Dimmer(LocationId dimmerName, int boardAddress, int channel, List<GroupAddress> switchGroupAddresses, List<GroupAddress> feedbackGroupAddresses,
			List<GroupAddress> switchLedControlGroupAddresses, List<GroupAddress> outputSwitchUpdateGroupAddresses) throws IOException {

		this.dimmerName = dimmerName;
		this.boardAddress = boardAddress;
		this.channel = channel;
		this.switchGroupAddresses = switchGroupAddresses;
		this.feedbackGroupAddresses = feedbackGroupAddresses;
		this.switchLedControlGroupAddresses = switchLedControlGroupAddresses;
		this.pc = getInstance().getKnxConnectionFactory().createProcessCommunicator();
	}

	public void run() {

		while (true) {
			try {
				DimmerCommand dimmerCommand = commandQueue.take();
				interupt.set(false);
				boolean feedbackSend = false;

				if (dimmerCommand.getSceneContext().isPresent()) {
					if (dimmerCommand.getSceneContext().get().isSceneActive()) {
						lastDimCommand = empty();
						activeScene = of(dimmerCommand);
						activateSceneLeds();
					} else {
						activeScene = empty();
						dimmerCommand = dimmerCommand.isUseThisDimCommandOnSceneDeativate() ? dimmerCommand : lastDimCommand.orElse(dimmerCommand);
					}
				} else {
					lastDimCommand = of(dimmerCommand);
					if ((dimmerCommand.getTargetVal().compareTo(ZERO) == 0)) {
						if ((activeScene.isPresent())) {
							activateSceneLeds();
							dimmerCommand = activeScene.get();
							sendFeedback(dimmerCommand.getTargetVal().intValue(), true);
							feedbackSend = true;
						}
					} else {
						activateAllLedsAndFeedback();
						sleep(delayBeforeIncreasingDimValue);
					}
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

				if (!feedbackSend && !getInstance().getLoxoneIa().equals(dimmerCommand.getOrigin())) {
					sendFeedback(getCurVal().intValue(), false);
				}

				if (curVal.compareTo(ZERO) == 0) {
					for (int i = 0; i < turnOffDelay; i++) {
						sleep(1);
						if (interupt.get()) {
							break;
						}
					}

					if (!interupt.get()) {
						for (GroupAddress groupAddress : union(union(switchGroupAddresses, switchLedControlGroupAddresses), outputSwitchUpdateGroupAddresses)) {
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

	private void activateAllLedsAndFeedback() throws Exception {
		for (GroupAddress groupAddress : union(union(switchGroupAddresses, switchLedControlGroupAddresses), outputSwitchUpdateGroupAddresses)) {
			pc.write(groupAddress, true);
		}
	}

	private void activateSceneLeds() throws Exception {
		for (GroupAddress groupAddress : union(union(switchGroupAddresses, switchLedControlGroupAddresses), outputSwitchUpdateGroupAddresses)) {
			pc.write(groupAddress, false);
		}
		for (GroupAddress groupAddress : activeScene.get().getSceneContext().get().getSceneParticipants()) {
			pc.write(groupAddress, true);
		}
	}

	private void sendFeedback(int val, boolean refresh) throws Exception {
		for (GroupAddress groupAddress : feedbackGroupAddresses) {

			DPTXlator8BitUnsigned dDPTXlator8BitUnsigned = new DPTXlator8BitUnsigned(DPT_PERCENT_U8);
			if (refresh) {
				dDPTXlator8BitUnsigned.setValue(0);
				pc.write(groupAddress, dDPTXlator8BitUnsigned);
			}

			dDPTXlator8BitUnsigned.setValue(val);
			pc.write(groupAddress, dDPTXlator8BitUnsigned);
		}
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
		byte[] b = Support.convertPercentageToDacBytes(targetValue);
		getInstance().getI2CCommunicator().write(boardAddress, channel, b);
	}

	public BigDecimal getCurVal() {
		return curVal;
	}

	public DimDirection getLastDimDirection() {
		return lastDimDirection;
	}

	public Optional<DimmerCommand> getLastDimCommand() {
		return lastDimCommand;
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

	public KnxDimmerProcessListener getKnxDimmerProcessListener() {
		return knxDimmerProcessListener;
	}

	public void setKnxDimmerProcessListener(final KnxDimmerProcessListener knxDimmerProcessListener) {
		this.knxDimmerProcessListener = knxDimmerProcessListener;
	}
}

