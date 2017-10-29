package be.error.rpi.knx;

import tuwien.auto.calimero.DetachEvent;
import tuwien.auto.calimero.process.ProcessEvent;
import tuwien.auto.calimero.process.ProcessListenerEx;

/**
 * Created by koen on 04.10.16.
 */
public class ProcessListenerAdapter extends ProcessListenerEx {

	@Override
	public void groupReadRequest(final ProcessEvent e) {
		
	}

	@Override
	public void groupReadResponse(final ProcessEvent e) {

	}

	@Override
	public void groupWrite(final ProcessEvent e) {

	}

	@Override
	public void detached(final DetachEvent e) {

	}
}
