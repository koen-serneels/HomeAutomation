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

import static java.lang.String.format;
import static java.util.Optional.of;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import be.error.rpi.config.RunConfig;
import be.error.rpi.knx.KnxConnectionFactory;

public class Sonos extends Thread {

	private RestTemplate restTemplate = new RestTemplate();

	private URL sonosController;
	private String volume = "%s/kitchen/volume/%s";
	private String play = "%s/play";
	private String pause = "%s/pause";
	private String station = "%s/kitchen/tunein/play/%s";

	private static final Logger logger = LoggerFactory.getLogger(Sonos.class);

	private boolean lastVolumeToggleWasUp;

	private boolean on;
	private int curVol = 15;
	private Station curStation = Station.STUBRU;

	private final AtomicBoolean interupt = new AtomicBoolean(false);
	private final BlockingQueue<SonosCommand> commandQueue = new LinkedBlockingDeque();

	private KnxSonosProcessListener knxSonosProcessListener;

	public Sonos() throws MalformedURLException {
		this.sonosController = new URL("http://" + RunConfig.getInstance().getSonosControllerHost() + ":" + RunConfig.getInstance().getSonosControllerPort());
	}

	public void run() {
		while (true) {
			try {
				SonosCommand command = commandQueue.take();
				KnxConnectionFactory.getInstance().runWithProcessCommunicator(pc -> {
					interupt.set(false);

					if (command.isToggleVolume()) {
						lastVolumeToggleWasUp = !lastVolumeToggleWasUp;
						while (true) {
							if (!on) {
								SonosCommand sonosCommand = new SonosCommand();
								sonosCommand.setPlay(of(true));
								putCommand(sonosCommand);
								pc.write(knxSonosProcessListener.getPlayStop(), true);
								break;
							}
							if (lastVolumeToggleWasUp) {
								curVol += 1;
							} else {
								curVol -= 1;
							}

							if (curVol > 0 && curVol < 40) {
								volume();
							} else {
								break;
							}

							if (commandQueue.peek() != null) {
								break;
							}
							if (interupt.get()) {
								break;
							}
							sleep(100);
							if (interupt.get()) {
								break;
							}
						}
					} else if (command.getPlay().isPresent()) {
						if (command.getPlay().get()) {
							on = true;
							lastVolumeToggleWasUp = false;
							curVol = 20;
							volume();
							play();
							station();
						} else {
							on = false;
							pause();
						}
					} else if (command.isToggleStation()) {
						if (on) {
							curStation = Station.values()[curStation.ordinal() + 1 >= Station.values().length ? 0 : curStation.ordinal() + 1];
							station();
						}
					}
				});
			} catch (InterruptedException e) {
				logger.error("Sonos controller got interrupt", e);
				interrupt();
				//Do nothing
			} catch (Exception e) {
				logger.error("Sonos controller got exception", e);
				interrupt();
			}
		}
	}

	private void volume() {
		ResponseEntity<Void> response = restTemplate.getForEntity(format(volume, sonosController.toString(), curVol), Void.class);
		response.getBody();
	}

	private void play() {
		ResponseEntity<Void> response = restTemplate.getForEntity(format(play, sonosController.toString()), Void.class);
		response.getBody();
	}

	private void pause() {
		ResponseEntity<Void> response = restTemplate.getForEntity(format(pause, sonosController.toString()), Void.class);
		response.getBody();
	}

	private void station() {
		ResponseEntity<Void> response = restTemplate.getForEntity(format(station, sonosController.toString(), curStation.getStation()), Void.class);
		response.getBody();
	}

	public synchronized void putCommand(SonosCommand sonosCommand) throws Exception {
		commandQueue.clear();
		commandQueue.put(sonosCommand);
	}

	public void interrupt() {
		interupt.set(true);
	}

	public void setKnxDimmerProcessListener(final KnxSonosProcessListener knxSonosProcessListener) {
		this.knxSonosProcessListener = knxSonosProcessListener;
	}

	private enum Station {
		STUBRU("2611"),
		MNM("10861"),
		QMUSIC("2398");

		private String station;

		Station(final String station) {
			this.station = station;
		}

		public String getStation() {
			return station;
		}
	}
}

