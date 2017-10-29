package be.error.rpi.adc;

import static java.util.BitSet.valueOf;
import static java.util.Collections.unmodifiableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.i2c.I2CDevice;

/**
 * @author Koen Serneels
 */
public class Adc {

	private static final Logger logger = LoggerFactory.getLogger(Adc.class);

	private AdcConfiguration adcConfiguration;
	private final I2CDevice adcDevice;
	private final List<AdcChannel> adcChannels = new ArrayList<>();
	private final AdcBoard adcBoard;

	public Adc(AdcConfiguration adcConfiguration, AdcBoard adcBoard) {
		try {
			this.adcDevice = adcBoard.getBus().getDevice(adcConfiguration.getAddress());
			this.adcConfiguration = adcConfiguration;
			this.adcBoard = adcBoard;
		} catch (IOException ioException) {
			logger.error("Could not construct ADC",ioException);
			throw new RuntimeException(ioException);
		}
	}

	public <R> List<R> doWithChannels(Function<AdcChannel, R> function) {
		return adcChannels.stream().map(function).collect(Collectors.<R>toList());
	}

	public byte[] read(AdcChannel adcChannel) throws IOException {
		BitSet bitSet = new BitSet(8);
		adcConfiguration.configure(bitSet);

		setChannel(bitSet, adcChannel);
		write(bitSet);

		byte result[] = new byte[4];
		BitSet conversionBussy;

		do {
			adcDevice.read(result, 0, 3);
			conversionBussy = valueOf(new byte[] { result[2] });
		} while (conversionBussy.get(7));

		return result;
	}

	public void write(BitSet bitSet) {
		try {
			adcDevice.write(bitSet.toByteArray()[0]);
		} catch (IOException ioException) {
			logger.error("Failed writing to I2CDevice " + adcConfiguration.getAddress(), ioException);
			throw new RuntimeException(ioException);
		}
	}

	private BitSet setChannel(BitSet bitSet, AdcChannel adcChannel) {
		BitSet chan = valueOf(new byte[] { (byte) (adcChannel.getChannel() - 1) });
		bitSet.set(5, chan.get(0));
		bitSet.set(6, chan.get(1));
		return bitSet;
	}

	public I2CDevice getAdcDevice() {
		return adcDevice;
	}

	public void addChannel(String description, String id) {
		adcChannels.add(new AdcChannel(adcChannels.size(), this, description, id));
	}

	public List<AdcChannel> getAdcChannels() {
		return unmodifiableList(adcChannels);
	}

	public AdcConfiguration getAdcConfiguration() {
		return adcConfiguration;
	}

	public AdcBoard getAdcBoard() {
		return adcBoard;
	}
}
