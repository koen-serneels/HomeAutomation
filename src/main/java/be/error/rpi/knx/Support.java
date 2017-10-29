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
package be.error.rpi.knx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.exception.KNXException;

public class Support {

	private static final Logger logger = LoggerFactory.getLogger(Support.class);

	public static GroupAddress createGroupAddress(String groupAddress) {
		try {
			return new GroupAddress(groupAddress);
		} catch (KNXException knxException) {
			logger.error("Could not create group address from " + groupAddress, knxException);
			throw new RuntimeException(knxException);
		}
	}
}
