package be.error.rpi.adc;

import static be.error.rpi.adc.ObjectStatusTypeMapper.ObjectStatusType.READ_ERROR;
import static java.math.RoundingMode.HALF_UP;
import static java.util.BitSet.valueOf;
import static org.apache.commons.lang3.tuple.Pair.of;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.BitSet;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.error.rpi.adc.ObjectStatusTypeMapper.ObjectStatusType;

/**
 * @author Koen Serneels
 */
public class ObjectStatusReader implements Function<AdcChannel, Pair<AdcChannel, ObjectStatusType>> {

	private static final Logger logger = LoggerFactory.getLogger(ObjectStatusReader.class);

	private static final BigDecimal divider = new BigDecimal("2").pow(11);
	private static final BigDecimal step = new BigDecimal(5).divide(divider);

	private ObjectStatusTypeMapper objectStatusTypeMapper = new ObjectStatusTypeMapper();

	@Override
	public Pair<AdcChannel, ObjectStatusType> apply(final AdcChannel adcChannel) {
		try {
			byte readResult[] = adcChannel.getAdc().read(adcChannel);
			BitSet conversion = valueOf(new byte[] { readResult[1], readResult[0] });

			ObjectStatusType objectStatusType = READ_ERROR;

			if (!conversion.get(15)) {
				long[] l = conversion.toLongArray();
				if (l != null && l.length == 1) {
					BigDecimal value = new BigDecimal(l[0]);
					BigDecimal voltage = value.multiply(step).setScale(2, HALF_UP);
					objectStatusType = objectStatusTypeMapper.map(voltage);
				}
			}
			return of(adcChannel, objectStatusType);
		} catch (IOException ioException) {
			logger.error("Could not read from channel", ioException);
			throw new RuntimeException(ioException);
		}
	}
}
