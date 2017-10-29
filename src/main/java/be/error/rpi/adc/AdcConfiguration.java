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

import java.util.BitSet;

/**
 * @author Koen Serneels
 */
public class AdcConfiguration {

	public int address;

	public AdcConfiguration(int address) {
		this.address = address;
	}

	public BitSet configure(BitSet bitSet) {
		setGain1(bitSet);
		setBitRate12(bitSet);
		setContinousOn(bitSet);
		return bitSet;
	}

	public BitSet setGain1(BitSet bitSet) {
		bitSet.set(0, false);
		bitSet.set(1, false);
		return bitSet;
	}

	public BitSet setBitRate12(BitSet bitSet) {
		bitSet.set(2, false);
		bitSet.set(3, false);
		return bitSet;
	}

	public BitSet setContinousOn(BitSet bitSet) {
		bitSet.set(4, true);
		return bitSet;
	}

	public int getAddress() {
		return address;
	}
}



