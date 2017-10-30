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
package be.error.rpi.dac.dimmer.builder;

import static be.error.rpi.config.RunConfig.getInstance;
import static be.error.rpi.dac.dimmer.builder.DimDirection.DOWN;
import static be.error.rpi.dac.dimmer.builder.DimDirection.UP;
import static be.error.rpi.dac.dimmer.builder.DimmerBackend.I2C;
import static be.error.rpi.dac.support.Support.convertPercentageTo10Volt;
import static be.error.rpi.dac.support.Support.convertPercentageToDacBytes;
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

import lucidio.LucidControlAO4;
import lucidio.ValueVOS2;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned;
import tuwien.auto.calimero.process.ProcessCommunicator;

import be.error.rpi.knx.KnxConnectionFactory;
import be.error.types.LocationId;

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
	private final DimmerBackend dimmerBackend;
	private final int boardAddress;
	private final int channel;

	private KnxDimmerProcessListener knxDimmerProcessListener;

	private Optional<DimmerCommand> lastDimCommand = empty();
	private Optional<DimmerCommand> activeScene = empty();

	public Dimmer(LocationId dimmerName, DimmerBackend dimmerBackend, int boardAddress, int channel, List<GroupAddress> switchGroupAddresses,
			List<GroupAddress> feedbackGroupAddresses, List<GroupAddress> switchLedControlGroupAddresses, List<GroupAddress> outputSwitchUpdateGroupAddresses)
			throws IOException {

		this.dimmerName = dimmerName;
		this.dimmerBackend = dimmerBackend;
		this.boardAddress = boardAddress;
		this.channel = channel;
		this.switchGroupAddresses = switchGroupAddresses;
		this.feedbackGroupAddresses = feedbackGroupAddresses;
		this.switchLedControlGroupAddresses = switchLedControlGroupAddresses;
	}

	public void run() {
		while (true) {
			try {
				DimmerCommand command = commandQueue.take();

				KnxConnectionFactory.getInstance().runWithProcessCommunicator(pc -> {
					DimmerCommand dimmerCommand = command;
					interupt.set(false);
					boolean feedbackSend = false;

					if (dimmerCommand.getSceneContext().isPresent()) {
						if (dimmerCommand.getSceneContext().get().isSceneActive()) {
							lastDimCommand = empty();
							activeScene = of(dimmerCommand);
							activateSceneLeds(pc);
						} else {
							activeScene = empty();
							dimmerCommand = dimmerCommand.isUseThisDimCommandOnSceneDeativate() ? dimmerCommand : lastDimCommand.orElse(dimmerCommand);
						}
					} else {
						lastDimCommand = of(dimmerCommand);
						if ((dimmerCommand.getTargetVal().compareTo(ZERO) == 0)) {
							if ((activeScene.isPresent())) {
								activateSceneLeds(pc);
								dimmerCommand = activeScene.get();
								sendFeedback(pc, dimmerCommand.getTargetVal().intValue(), true);
								feedbackSend = true;
							}
						} else {
							activateAllLedsAndFeedback(pc);
							sleep(delayBeforeIncreasingDimValue);
						}
					}

					if (dimmerCommand.getTargetVal().compareTo(curVal) > 0) {
						lastDimDirection = UP;
					} else if (dimmerCommand.getTargetVal().compareTo(curVal) < 0) {
						lastDimDirection = DOWN;
					} else {
						lastDimDirection = (lastDimDirection == UP ? DOWN : UP);
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
						sendFeedback(pc, getCurVal().intValue(), false);
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
				});
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
		commandQueue.clear();
		commandQueue.put(dimmerCommand);
	}

	public void interrupt() {
		interupt.set(true);
	}

	private void activateAllLedsAndFeedback(ProcessCommunicator pc) throws Exception {
		for (GroupAddress groupAddress : union(union(switchGroupAddresses, switchLedControlGroupAddresses), outputSwitchUpdateGroupAddresses)) {
			pc.write(groupAddress, true);
		}
	}

	private void activateSceneLeds(ProcessCommunicator pc) throws Exception {
		for (GroupAddress groupAddress : union(union(switchGroupAddresses, switchLedControlGroupAddresses), outputSwitchUpdateGroupAddresses)) {
			pc.write(groupAddress, false);
		}
		for (GroupAddress groupAddress : activeScene.get().getSceneContext().get().getSceneParticipants()) {
			pc.write(groupAddress, true);
		}
	}

	private void sendFeedback(ProcessCommunicator pc, int val, boolean refresh) throws Exception {
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
		if (dimmerBackend == I2C) {
			byte[] b = convertPercentageToDacBytes(targetValue);
			getInstance().getI2CCommunicator().write(boardAddress, channel, b);
		} else {
			LucidControlAO4 lucidControlAO4 = getInstance().getLucidControlAO4(boardAddress);
			lucidControlAO4.setIo(channel, new ValueVOS2(convertPercentageTo10Volt(targetValue)));
		}
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

