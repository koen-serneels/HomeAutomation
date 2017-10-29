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
package be.error.rpi.adc;

import static java.util.Arrays.asList;

import java.util.BitSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.pi4j.io.i2c.I2CBus;

/**
 * @author Koen Serneels
 */
public class AdcBoard {

	private final I2CBus bus;

	private Adc leftAdc;
	private Adc rightAdc;

	private boolean initialized;

	public AdcBoard(I2CBus bus) {
		this.bus = bus;
	}

	public <R> List<R> read(Function<AdcChannel, R> function) {
		return asList(leftAdc, rightAdc).stream().flatMap(adc -> adc.doWithChannels(function).stream()).collect(Collectors.toList());
	}

	protected void initialize() {
		asList(leftAdc, rightAdc).stream().forEach(adc -> {
			BitSet bitSet = new BitSet(8);
			bitSet.set(7);
			adc.write(adc.getAdcConfiguration().configure(bitSet));
		});
		initialized = true;
	}

	public void setLeftAdc(Adc leftAdc) {
		this.leftAdc = leftAdc;
	}

	public void setRightAdc(Adc rightAdc) {
		this.rightAdc = rightAdc;
	}

	public I2CBus getBus() {
		return bus;
	}
}
