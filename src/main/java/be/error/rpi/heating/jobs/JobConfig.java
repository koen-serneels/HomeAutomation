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
package be.error.rpi.heating.jobs;

import java.io.Serializable;
import java.util.Map;

import tuwien.auto.calimero.GroupAddress;

import be.error.rpi.ebus.EbusCommand;
import be.error.rpi.heating.EbusDeviceAddress;

public class JobConfig implements Serializable {

	private EbusDeviceAddress ebusDeviceAddress;
	private Map<EbusCommand<?>, GroupAddress> ebusCommands;

	public JobConfig(final EbusDeviceAddress ebusDeviceAddress, final Map<EbusCommand<?>, GroupAddress> ebusCommands) {
		this.ebusDeviceAddress = ebusDeviceAddress;
		this.ebusCommands = ebusCommands;
	}

	public EbusDeviceAddress getEbusDeviceAddress() {
		return ebusDeviceAddress;
	}

	public Map<EbusCommand<?>, GroupAddress> getEbusCommands() {
		return ebusCommands;
	}
}
