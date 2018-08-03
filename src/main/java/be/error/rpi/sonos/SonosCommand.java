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

import static java.util.Optional.empty;

import java.util.Optional;

public class SonosCommand {

	private boolean toggleVolume;
	private Optional<Boolean> play = empty();
	private boolean toggleStation;

	public boolean isToggleVolume() {
		return toggleVolume;
	}

	public void setToggleVolume(final boolean toggleVolume) {
		this.toggleVolume = toggleVolume;
	}

	public Optional<Boolean> getPlay() {
		return play;
	}

	public void setPlay(final Optional<Boolean> play) {
		this.play = play;
	}

	public boolean isToggleStation() {
		return toggleStation;
	}

	public void setToggleStation(final boolean toggleStation) {
		this.toggleStation = toggleStation;
	}
}
