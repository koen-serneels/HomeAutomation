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

	private boolean override;

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

	public void setOverride() {
		override = true;
	}

	public boolean isOverride() {
		return override;
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
