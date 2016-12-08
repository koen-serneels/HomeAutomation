package be.error.rpi.dac.dimmer.builder;

import static be.error.rpi.dac.dimmer.builder.DimDirection.UP;
import static tuwien.auto.calimero.process.ProcessCommunicationBase.SCALING;

import java.math.BigDecimal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.DetachEvent;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.process.ProcessEvent;

/**
 * @author Koen Serneels
 */
public class KnxDimmerProcessListener extends AbstractDimmerProcessListener {

	protected static final Logger logger = LoggerFactory.getLogger(KnxDimmerProcessListener.class);

	private final GroupAddress onOff;
	private final Optional<GroupAddress> dim;
	private final Optional<GroupAddress> dimAbsolute;
	private int minDimVal = 1;

	KnxDimmerProcessListener(final GroupAddress onOff, final Optional<GroupAddress> dim, final Optional<GroupAddress> dimAbsolute, final Optional<Integer> minDimVal,
			Dimmer dimmer) {
		this.onOff = onOff;
		this.dim = dim;
		this.dimAbsolute = dimAbsolute;
		if (minDimVal.isPresent()) {
			this.minDimVal = minDimVal.get();
		}

		setDimmer(dimmer);
	}

	@Override
	public void groupWrite(final ProcessEvent e) {
		if (dim.isPresent() && e.getDestination().equals(dim.get())) {
			try {
				dimmer.interrupt();
				boolean b = asBool(e);

				if (b) {
					dimmer.putCommand(new DimmerCommand(dimmer.getLastDimDirection() == UP ? new BigDecimal(minDimVal) : new BigDecimal(100), e.getSourceAddr()));
				}
			} catch (Exception ex) {
				logger.error("Could not process KNX dim command", ex);
			}
		}

		if (dimAbsolute.isPresent() && e.getDestination().equals(dimAbsolute.get())) {
			try {
				dimmer.interrupt();
				int i = asUnsigned(e, SCALING);
				dimmer.putCommand(new DimmerCommand(new BigDecimal(i), e.getSourceAddr()));
			} catch (Exception ex) {
				logger.error("Could not process KNX dim command", ex);
			}
		}

		if (e.getDestination().equals(onOff)) {
			try {
				dimmer.interrupt();
				boolean b = asBool(e);
				dimmer.putCommand(new DimmerCommand(b ? new BigDecimal(100) : new BigDecimal(0), e.getSourceAddr()));
			} catch (Exception ex) {
				logger.error("Could not process KNX dim command", ex);
			}
		}
	}

	@Override
	public void groupReadRequest(final ProcessEvent e) {

	}

	@Override
	public void groupReadResponse(final ProcessEvent e) {

	}

	@Override
	public void detached(final DetachEvent e) {

	}

	public boolean isDimmerGroupAddress(GroupAddress groupAddress) {
		if (onOff.equals(groupAddress)) {
			return true;
		}

		if (dim.isPresent() && dim.get().equals(groupAddress)) {
			return true;
		}

		if (dimAbsolute.isPresent() && dim.get().equals(groupAddress)) {
			return true;
		}

		return false;
	}

	public int getMinDimVal() {
		return minDimVal;
	}
}
