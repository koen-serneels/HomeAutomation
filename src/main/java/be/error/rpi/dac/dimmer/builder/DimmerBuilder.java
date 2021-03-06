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
import static be.error.rpi.dac.dimmer.builder.DimmerBackend.I2C;
import static be.error.rpi.dac.dimmer.builder.DimmerBackend.LUCID_CONTROL;
import static be.error.rpi.knx.Support.createGroupAddress;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.GroupAddress;

import be.error.types.LocationId;

/**
 * @author Koen Serneels
 */
public class DimmerBuilder {

	private static final Logger logger = LoggerFactory.getLogger(DimmerBuilder.class);

	private LocationId name;
	private DimmerBackend dimmerBackend = I2C;
	private int boardAddress;
	private int channel;

	private Optional<Long> turnOffDelay = empty();
	private Optional<Long> delayBeforeIncreasingDimValue = empty();
	private Optional<Integer> minDimVal = empty();
	private Optional<Integer> stepDelay = empty();

	//GA's that are read from the bus (~input)
	private Optional<GroupAddress> onOff = empty();
	private Optional<GroupAddress> precenseDetectorLock = empty();
	private Optional<GroupAddress> onOffOverride = empty();
	private Optional<GroupAddress> dim = empty();
	private Optional<GroupAddress> dimAbsolute = empty();
	private Optional<GroupAddress> dimAbsoluteOverride = empty();
	private Optional<GroupAddress> deactivateOnOtherUse = empty();

	//GA's that are written to (~output)
	private List<GroupAddress> switchGroupAddresses = new ArrayList();
	private List<GroupAddress> switchLedControlGroupAddresses = new ArrayList();
	private List<GroupAddress> switchUpdateGroupAddresses = new ArrayList();
	private List<GroupAddress> feedbackGroupAddresses = new ArrayList();

	/**
	 * The logical name of this dimmers instance. This is just an identifier for naming thread groups and debugging purposes
	 */
	public DimmerBuilder name(LocationId name) {
		this.name = name;
		return this;
	}

	/**
	 * The address of the module to which the analog in of the dimmers is connected
	 */
	public DimmerBuilder boardAddress(int boardAddress) {
		this.boardAddress = boardAddress;
		return this;
	}

	/**
	 * The specific channel of the RPI DAC extension board to which the analog in of the dimmers is connected
	 */
	public DimmerBuilder boardChannel(int channel) {
		this.channel = channel;
		return this;
	}

	/**
	 * When the dim value is > 0% this dimmers instance will need to turn on the LED driver. This is done by toggling a KNX actor. Likewise, when the dim value is at 0%,
	 * the LED driver needs to be turned off again. Add the KNX GA's here that operate the actor(s) if applicable
	 */
	public DimmerBuilder outputGroupAddressesForActorSwitchingOnAndOff(String... groupAddresses) {
		switchGroupAddresses.addAll(asList(groupAddresses).stream().map(s -> createGroupAddress(s)).collect(toList()));
		return this;
	}

	public DimmerBuilder outputGroupAddressesForSwitchLedControl(String... groupAddresses) {
		switchLedControlGroupAddresses.addAll(asList(groupAddresses).stream().map(s -> createGroupAddress(s)).collect(toList()));
		return this;
	}

	public DimmerBuilder outputGroupAddressesSwitchUpdate(String... groupAddresses) {
		switchUpdateGroupAddresses.addAll(asList(groupAddresses).stream().map(s -> createGroupAddress(s)).collect(toList()));
		return this;
	}

	/**
	 * Emmits KNX datagrams to the configured GA's when the dim value changes. This can be used to receive feedback (eg. for visualisation)
	 */
	public DimmerBuilder outputGroupAddressesForVisualisationStatusFeedback(String... groupAddresses) {
		feedbackGroupAddresses.addAll(asList(groupAddresses).stream().map(s -> createGroupAddress(s)).collect(toList()));
		return this;
	}

	/**
	 * When the light is turned off, keep the light at min. dim value for the stated delay. Then turn it off completely.
	 */
	public DimmerBuilder turnOffDelay(final long turnOffDelay) {
		this.turnOffDelay = of(turnOffDelay);
		return this;
	}

	/**
	 * To get a good soft-on effect, wait a short amount of time (delayBeforeIncreasingDimValue) after turning the LED on but before starting to increasing the dim
	 * value to the desired value. There is always a short turn-on delay for the LED itself. If the dimmers starts to increase light value before the LED is
	 * actually on it will reduce the soft-on effect. This timing could be slighty adjusted (couple of milliseconds) for each dimmers group depending
	 * on driver, amount of LED's on a single driver, the speed of the KNX actor and so forth
	 */
	public DimmerBuilder delayBeforeIncreasingDimValue(final long delayBeforeIncreasingDimValue) {
		this.delayBeforeIncreasingDimValue = of(delayBeforeIncreasingDimValue);
		return this;
	}

	public DimmerBuilder precenseOperated(String precenseDetectorLock) {
		this.precenseDetectorLock = of(createGroupAddress(precenseDetectorLock));
		return this;
	}

	/**
	 */
	public DimmerBuilder inputGroupAddressForOnOffOverride(String onOffOverride) {
		this.onOffOverride = isEmpty(onOffOverride) ? empty() : of(createGroupAddress(onOffOverride));
		return this;
	}

	/**
	 * Will listen for telegrams on the given GA which are considered on/off (1bit boolean) telegrams for toggling the LED on and off.
	 * Mostly this will be the switch FO of a KNX taster that will be bound to this GA.
	 */
	public DimmerBuilder inputGroupAddressForOnAndOff(String onOff) {
		this.onOff = of(createGroupAddress(onOff));
		return this;
	}

	/**
	 * Will listen for telegrams on the given GA which are considered dim start and dim stop (1bit boolean) telegrams for starting the dim sequence. This GA does NOT
	 * WORK WITH PERCENTAGES. When a telegram is received on this GA that is '1' or 'true', dimming will automatically start. It will only stop when a new telegram is
	 * received that is '0' or 'false'. Mostly this will be the dim FO of a KNX taster that will be bound to this GA. The dim FO will automatically send a '1' telegram
	 * when the user pushes (and keeps pushing) the button longer than x msec the button goes to dimming mode and the dim FO will send a '1' to this GA, meaning 'start
	 * dimming'. When the user releases the button, the FO will automatically send a 'on release' telegram being '0' which will make the dimming stop at te current value
	 */
	public DimmerBuilder inputGroupAddressForDimStartAndStop(String dim) {
		this.dim = of(createGroupAddress(dim));
		return this;
	}

	/**
	 * Will listen for telegrams on the given GA which are considered percentage telegrams for bringing the dimmer to the desired value
	 */
	public DimmerBuilder inputGroupAddressForAbsoluteDimValue(String dim) {
		this.dimAbsolute = of(createGroupAddress(dim));
		return this;
	}

	/**
	 * Will listen for telegrams on the given GA which are considered percentage telegrams for bringing the dimmer to the desired value
	 */
	public DimmerBuilder inputGroupAddressForAbsoluteDimValueOverride(String dim) {
		this.dimAbsoluteOverride = of(createGroupAddress(dim));
		return this;
	}

	/**
	 * When an "on" is sent on this GA, the current dimmer will be suspended to allow to re-use the same button to do other things as long as the other use is enabled.
	 * From the moment an "off" is received, the dimmer continues normal operation
	 */
	public DimmerBuilder inputGroupAddressForDeactiveOnOtherUse(String dim) {
		this.deactivateOnOtherUse = of(createGroupAddress(dim));
		return this;
	}

	/**
	 * The dimming will not go below this value. This is important as certain LED drivers turn themselves off if the analog 0-10v input goes lower than a specific
	 * voltage. By setting the min dim value the LED cannot be 'dimmed to off'. This increases user experience as the LED can now not be accidentally turned off when
	 * the user wants to dim to minimum. In other words, if dimming stops, the user knows the LED is at it's minimum.
	 */
	public DimmerBuilder minimumDimValue(int minDimVal) {
		this.minDimVal = of(minDimVal);
		return this;
	}

	/**
	 * Waits the specific amount (in msec) between each dimming step. Basically this determines the dimming speed. The higher the delay, the longer it takes to reach
	 * the min or max dim value. Likewise, the lower the number the faster min or max dim value will be reached
	 */
	public DimmerBuilder delayBetweenDimmingSteps(int stepDelay) {
		this.stepDelay = of(stepDelay);
		return this;
	}

	public void lucidControl() {
		this.dimmerBackend = LUCID_CONTROL;
	}

	public Dimmer build() {
		try {
			Dimmer dimmer = new Dimmer(name, dimmerBackend, boardAddress, channel, switchGroupAddresses, feedbackGroupAddresses, switchLedControlGroupAddresses,
					switchUpdateGroupAddresses);

			if (delayBeforeIncreasingDimValue.isPresent()) {
				dimmer.setDelayBeforeIncreasingDimValue(delayBeforeIncreasingDimValue.get());
			}
			if (turnOffDelay.isPresent()) {
				dimmer.setTurnOffDelay(turnOffDelay.get());
			}

			if (stepDelay.isPresent()) {
				dimmer.setStepDelay(stepDelay.get());
			}

			dimmer.start();

			KnxDimmerProcessListener knxDimmerProcessListener = new KnxDimmerProcessListener(onOff, onOffOverride, precenseDetectorLock, dim, dimAbsolute,
					dimAbsoluteOverride, minDimVal, deactivateOnOtherUse, dimmer);
			getInstance().getKnxConnectionFactory().addProcessListener(knxDimmerProcessListener);
			dimmer.setKnxDimmerProcessListener(knxDimmerProcessListener);
			return dimmer;
		} catch (Exception e) {
			logger.error("Could not build dimmers", e);
			throw new RuntimeException(e);
		}
	}
}
