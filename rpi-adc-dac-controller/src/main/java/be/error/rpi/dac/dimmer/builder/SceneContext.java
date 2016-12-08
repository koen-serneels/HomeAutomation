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
