/*-
 * #%L
 * Home Automation
 * %%
 * Copyright (C) 2016 - 2018 Koen Serneels
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
package be.error.rpi.sonos;

import static java.util.Optional.of;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.DetachEvent;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.process.ProcessEvent;
import tuwien.auto.calimero.process.ProcessListenerEx;

public class KnxSonosProcessListener extends ProcessListenerEx {

	protected static final Logger logger = LoggerFactory.getLogger(KnxSonosProcessListener.class);

	private final Sonos sonosController;
	private final GroupAddress playStop;
	private final GroupAddress volumeUpDown;
	private final Collection<GroupAddress> stationSelect;

	public KnxSonosProcessListener(Sonos sonosController, final GroupAddress playStop, final GroupAddress volumeUpDown, final Collection<GroupAddress> stationSelect) {
		this.sonosController = sonosController;
		this.playStop = playStop;
		this.volumeUpDown = volumeUpDown;
		this.stationSelect = stationSelect;
	}

	@Override
	public void groupWrite(final ProcessEvent e) {
		try {
			if (e.getDestination().equals(playStop)) {
				sonosController.interrupt();
				boolean b = asBool(e);
				SonosCommand sonosCommand = new SonosCommand();
				sonosCommand.setPlay(of(b));
				sonosController.putCommand(sonosCommand);
			}

			if (e.getDestination().equals(volumeUpDown)) {
				sonosController.interrupt();
				boolean b = asBool(e);
				if (b) {
					SonosCommand sonosCommand = new SonosCommand();
					sonosCommand.setToggleVolume(true);
					sonosController.putCommand(sonosCommand);
				}
			}

			if (stationSelect.contains(e.getDestination())) {
				sonosController.interrupt();
				SonosCommand sonosCommand = new SonosCommand();
				sonosCommand.setToggleStation(true);
				sonosController.putCommand(sonosCommand);
			}
		} catch (Exception ex) {
			logger.error("Could not process KNX sonos command", ex);
		}
	}

	@Override
	public void groupReadRequest(final ProcessEvent e) {

	}

	@Override
	public void groupReadResponse(final ProcessEvent processEvent) {

	}

	@Override
	public void detached(final DetachEvent detachEvent) {

	}

	public GroupAddress getPlayStop() {
		return playStop;
	}
}
