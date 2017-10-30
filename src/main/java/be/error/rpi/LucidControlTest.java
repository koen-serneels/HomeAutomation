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
package be.error.rpi;

import lucidio.LucidControlAO4;
import lucidio.ValueVOS2;
import lucidio.ValueVOS4;

/**
 * Created by koen on 30.04.17.
 */
public class LucidControlTest {

	public static void main(String[] args) throws Exception {
		LucidControlAO4 lucidControlAO4 = new LucidControlAO4("/dev/ttyACM0");
		lucidControlAO4.open();

		for (double i = 0; i <= 10; i = i + 0.1) {
			lucidControlAO4.setIo(0, new ValueVOS2(i));
		}
		ValueVOS4 val = new ValueVOS4();
		lucidControlAO4.getIo(0, val);
		System.err.println("Value after:" + val.getVoltage());

	}
}
