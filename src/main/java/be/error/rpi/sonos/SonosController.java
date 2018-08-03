/*-
 * #%L
 * Home Automation
 * %%
 * Copyright (C) 2016 - 2018 Koen Serneels
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
package be.error.rpi.sonos;

import static be.error.rpi.knx.Support.createGroupAddress;
import static java.util.Arrays.asList;

import java.net.MalformedURLException;

import be.error.rpi.config.RunConfig;

public class SonosController {

	public void start() throws MalformedURLException {
		Sonos sonos = new Sonos();
		KnxSonosProcessListener knxSonosProcessListener = new KnxSonosProcessListener(sonos, createGroupAddress("15/1/0"), createGroupAddress("15/1/1"),
				asList(createGroupAddress("15/1/2"), createGroupAddress("5/1/5"), createGroupAddress("5/1/6")));
		sonos.setKnxDimmerProcessListener(knxSonosProcessListener);
		RunConfig.getInstance().getKnxConnectionFactory().addProcessListener(knxSonosProcessListener);
		sonos.start();
	}
}
