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

/**
 * @author Koen Serneels
 */
class AdcChannel {

	private int channel;
	private String object;
	private String id;
	private Adc adc;

	public AdcChannel(final int channel, final Adc adc, final String object, final String id) {
		this.channel = channel;
		this.object = object;
		this.adc = adc;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public int getChannel() {
		return channel;
	}

	public String getObject() {
		return object;
	}

	public Adc getAdc() {
		return adc;
	}
}
