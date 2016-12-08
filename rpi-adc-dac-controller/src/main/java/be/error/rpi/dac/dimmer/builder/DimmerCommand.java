package be.error.rpi.dac.dimmer.builder;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.math.BigDecimal;
import java.util.Optional;

import tuwien.auto.calimero.IndividualAddress;

/**
 * @author Koen Serneels
 */
public class DimmerCommand {

	private BigDecimal targetVal;
	private IndividualAddress origin;

	private boolean useThisDimCommandOnSceneDeativate;
	private Optional<SceneContext> sceneContext = empty();

	public DimmerCommand(final BigDecimal targetVal, final IndividualAddress origin, SceneContext sceneContext, boolean useThisDimCommandOnSceneDeativate) {
		this(targetVal, origin, sceneContext);
		this.useThisDimCommandOnSceneDeativate = useThisDimCommandOnSceneDeativate;
	}

	public DimmerCommand(final BigDecimal targetVal, final IndividualAddress origin, SceneContext sceneContext) {
		this(targetVal, origin);
		this.sceneContext = of(sceneContext);
	}

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

	public Optional<SceneContext> getSceneContext() {
		return sceneContext;
	}

	public boolean isUseThisDimCommandOnSceneDeativate() {
		return useThisDimCommandOnSceneDeativate;
	}

	@Override
	public String toString() {
		return "DimmerCommand{" + "targetVal=" + targetVal + '}';
	}
}
