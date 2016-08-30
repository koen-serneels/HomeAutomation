package be.error.rpi.dac.dimmer.builder;

import static be.error.rpi.config.RunConfig.getInstance;
import static be.error.rpi.dac.dimmer.builder.DimmerSupport.createGroupAddress;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.GroupAddress;

import be.error.rpi.dac.dimmer.config.DimmerName;

/**
 * @author Koen Serneels
 */
public class DimmerBuilder {

	private static final Logger logger = LoggerFactory.getLogger(DimmerBuilder.class);

	private DimmerName name;
	private int boardAddress;
	private int channel;

	private Optional<Long> turnOffDelay = empty();
	private Optional<Long> delayBeforeIncreasingDimValue = empty();
	private Optional<Integer> minDimVal = empty();
	private Optional<Integer> stepDelay = empty();

	//GA's that are read from the bus (~input)
	private GroupAddress onOff;
	private Optional<GroupAddress> dim = empty();
	private Optional<GroupAddress> dimAbsolute = empty();

	//GA's that are written to (~output)
	private List<GroupAddress> switchGroupAddresses = new ArrayList();
	private List<GroupAddress> feedbackGroupAddresses = new ArrayList();

	/**
	 * The logical name of this dimmers instance. This is just an identifier for naming thread groups and debugging purposes
	 */
	public DimmerBuilder name(DimmerName name) {
		this.name = name;
		return this;
	}

	/**
	 * The I2C address of the RPI DAC extension board to which the analog in of the dimmers is connected
	 */
	public DimmerBuilder ic2BoardAddress(int boardAddress) {
		this.boardAddress = boardAddress;
		return this;
	}

	/**
	 * The specific channel of the RPI DAC extension board to which the analog in of the dimmers is connected (starts with 0)
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

	/**
	 * Emmits KNX datagrams to the configured GA's when the dim value changes. This can be used to receive feedback (eg. for visualisation)
	 */
	public DimmerBuilder outputGroupAddressesForVisualisationStatusFeedback(String... groupAddresses) {
		feedbackGroupAddresses.addAll(asList(groupAddresses).stream().map(s -> createGroupAddress(s)).collect(toList()));
		return this;
	}

	/**
	 * When the light is turned off, keep the light at min. dim value for the stated delay. Then turn it off completely.
	 * For default
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

	/**
	 * Will listen for telegrams on the given GA which are considered on/off (1bit boolean) telegrams for toggling the LED on and off.
	 * Mostly this will be the switch FO of a KNX taster that will be bound to this GA.
	 */
	public DimmerBuilder inputGroupAddressForOnAndOff(String onOff) {
		this.onOff = createGroupAddress(onOff);
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

	public Dimmer build() {
		try {
			Dimmer dimmer = new Dimmer(name, boardAddress, channel, switchGroupAddresses, feedbackGroupAddresses);

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

			KnxDimmerProcessListener knxDimmerProcessListener = new KnxDimmerProcessListener(onOff, dim, dimAbsolute, minDimVal, dimmer);
			getInstance().getKnxConnectionFactory().createProcessCommunicator(knxDimmerProcessListener);

			return dimmer;
		} catch (Exception e) {
			logger.error("Could not build dimmers", e);
			throw new RuntimeException(e);
		}
	}
}
