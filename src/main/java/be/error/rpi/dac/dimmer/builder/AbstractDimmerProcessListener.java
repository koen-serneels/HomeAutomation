package be.error.rpi.dac.dimmer.builder;

import tuwien.auto.calimero.process.ProcessListenerEx;

/**
 * @author Koen Serneels
 */
public abstract class AbstractDimmerProcessListener extends ProcessListenerEx {

	protected Dimmer dimmer;

	void setDimmer(Dimmer dimmer) {
		this.dimmer = dimmer;
	}
}
