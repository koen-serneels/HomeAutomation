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
package be.error.rpi.dac.support;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Koen Serneels
 */
public class Support {

	private static final Logger logger = LoggerFactory.getLogger(Support.class);

	public static byte[] convertPercentageToDacBytes(BigDecimal bigDecimal) {
		BigDecimal result = new BigDecimal(1023).divide(new BigDecimal(100)).multiply(bigDecimal).setScale(0, RoundingMode.HALF_UP);
		byte[] b = result.toBigInteger().toByteArray();
		if (b.length == 1) {
			return new byte[] { 0, b[0] };
		}
		return b;
	}

	public static double convertPercentageTo10Volt(BigDecimal bigDecimal) {
		BigDecimal result = bigDecimal.divide(new BigDecimal(10)).setScale(2, RoundingMode.HALF_UP);
		return result.doubleValue();
	}
}
