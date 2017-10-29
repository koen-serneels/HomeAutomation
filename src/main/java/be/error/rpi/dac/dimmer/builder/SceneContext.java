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

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;

import tuwien.auto.calimero.GroupAddress;

public class SceneContext {

	private boolean sceneActive;
	private Collection<GroupAddress> sceneParticipants = new ArrayList<>();

	public static SceneContext inactive() {
		return new SceneContext();
	}

	public static SceneContext active(GroupAddress... sceneParticipants) {
		SceneContext sceneContext = new SceneContext();
		sceneContext.sceneActive = true;
		sceneContext.sceneParticipants.addAll(asList(sceneParticipants));
		return sceneContext;
	}

	public boolean isSceneActive() {
		return sceneActive;
	}

	public Collection<GroupAddress> getSceneParticipants() {
		return sceneParticipants;
	}
}
