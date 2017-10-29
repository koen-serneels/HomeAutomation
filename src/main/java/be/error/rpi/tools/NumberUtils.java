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
package be.error.rpi.tools;

import java.math.BigDecimal;

public class NumberUtils {

	/**
	 * Convert number object to BigDecimal
	 *
	 * @param obj Any kind of primitive datatype
	 * @return A converted BigDecimal
	 */
	public static BigDecimal toBigDecimal(Object obj) {

		if (obj instanceof Integer) {
			return BigDecimal.valueOf((Integer) obj);
		} else if (obj instanceof Long) {
			return BigDecimal.valueOf((Long) obj);
		} else if (obj instanceof Short) {
			return BigDecimal.valueOf((Short) obj);
		} else if (obj instanceof Byte) {
			return BigDecimal.valueOf((Byte) obj);
		} else if (obj instanceof Double) {
			return BigDecimal.valueOf((Double) obj);
		} else if (obj instanceof Float) {
			return BigDecimal.valueOf((Float) obj);
		} else if (obj instanceof BigDecimal) {
			return (BigDecimal) obj;
		}

		return null;
	}
}
