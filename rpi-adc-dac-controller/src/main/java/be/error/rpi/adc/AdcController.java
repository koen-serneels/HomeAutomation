package be.error.rpi.adc;

import static be.error.rpi.config.RunConfig.getInstance;
import static java.lang.Thread.sleep;
import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.tuple.Pair.of;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.error.rpi.adc.ObjectStatusTypeMapper.ObjectStatusType;

/**
 * Loops over the different ADC connected to the I2C bus based on their bus address (see {@link AdcControllerConfiguration}). Each ADC board has two ADC
 * 'devices' and thus two addresses. Each ADC has 4 channels. Each channel is configured for continuous 16bit conversion. The application runs over each channel of
 * each devices and gathers the actual voltage. The voltage depends on the total resistance of the circuit. The magnetic contacts in use in use have 2 x 1kohm
 * resistors. When the contact is 'open' the 2 resistors are active. When the contact is 'closed', one resistor is bypassed. When the circuit voltage is measured via
 * the internal voltage divider of the ADC, this will result in different voltages. This results in 4 possible states and enables to detect circuit tampering. When the
 * circuit is short-circuited, the voltage measured will be near to the input voltage of 5V. When the circuit is cut (like a wire cut) a voltage near to 0V will be
 * measured. If the magnetic contact is open or closed a different voltage >0V and <5V will be measured. This is reflected in the following 4 enum states:
 * <p/>
 * <ul>
 * <li>{@link ObjectStatusType#OPEN}</li>
 * <li>{@link ObjectStatusType#CLOSED}</li>
 * <li>{@link ObjectStatusType#CIRCUIT_BREACH}</li>
 * <li>{@link ObjectStatusType#SHORT_CIRCUIT}</li>
 * </ul>
 * <p/>
 * There is an additional status for indicating read errors ({@link ObjectStatusType#READ_ERROR}) which means there is either a coding or a electronical issue. The
 * voltage measured is determined based on the ADC internal voltage divider and the total resistance at that time. See {@link ObjectStatusTypeMapper} for the
 * conversion table. Finally, the state read from each of the channels is sent over UDP to Loxone. Each channel is represented by a single UDP packet, consisting out
 * of 2 bytes payload. The first byte indicated the channel number, the second byte indicates the status. Via this information Loxone can display the state of each of
 * the window/door object.
 *
 * @author Koen Serneels
 */
public class AdcController {

	private static final Logger logger = LoggerFactory.getLogger(AdcController.class);

	private static final ExecutorService adcPool = newFixedThreadPool(4);

	public void run() throws Exception {
		ObjectStatusUdpSender sender = new ObjectStatusUdpSender(getInstance().getLoxoneIp(), getInstance().getLoxonePort());
		logger.debug("ADC controller starting");

		AdcControllerConfiguration adcControllerConfiguration = new AdcControllerConfiguration();

		int i = 0;
		while (true) {
			List<CompletableFuture<List<Pair<AdcChannel, ObjectStatusType>>>> futures = new ArrayList();

			for (AdcBoard adcBoard : adcControllerConfiguration.getAdcBoards(getInstance().getBus())) {
				futures.add(supplyAsync(() -> adcBoard.read(new ObjectStatusReader()), adcPool));
			}

			List<Pair<AdcChannel, ObjectStatusType>> results = allOf(futures.toArray(new CompletableFuture[0])).thenApply(v -> futures.stream().flatMap(f -> {
				try {
					return f.get().stream();
				} catch (Exception e) {
					logger.error("AdcController could not complete future", e);
					throw new RuntimeException();
				}
			})).get().collect(toList());

			sleep(1000);

			//Send internally
			getInstance().postAdcEvent(results.stream().map(p -> of(p.getLeft().getId(), p.getRight())).collect(toList()));

			//Only send every 2 seconds via UDP
			i++;
			if (i == 2) {
				sender.send(results);
				i = 0;
			}
		}
	}
}