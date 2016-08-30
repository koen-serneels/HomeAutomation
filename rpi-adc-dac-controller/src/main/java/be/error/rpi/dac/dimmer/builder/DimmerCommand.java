package be.error.rpi.dac.dimmer.builder;

import java.math.BigDecimal;

import tuwien.auto.calimero.IndividualAddress;

/**
 * @author Koen Serneels
 */
public class DimmerCommand {

	private BigDecimal targetVal;
	private IndividualAddress origin;

	public DimmerCommand(final BigDecimal targetVal, final IndividualAddress origin) {
		this.targetVal = targetVal;
		this.origin = origin;
	}

	public IndividualAddress getOrigin() {
		return origin;
	}

	public BigDecimal getTargetVal() {
		return targetVal;
	}

	@Override
	public String toString() {
		return "DimmerCommand{" + "targetVal=" + targetVal + '}';
	}
}
