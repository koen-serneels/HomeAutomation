package be.error.rpi.adc;

import static java.util.Arrays.asList;

import java.util.BitSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.pi4j.io.i2c.I2CBus;

/**
 * @author Koen Serneels
 */
public class AdcBoard {

	private final I2CBus bus;

	private Adc leftAdc;
	private Adc rightAdc;

	private boolean initialized;

	public AdcBoard(I2CBus bus) {
		this.bus = bus;
	}

	public <R> List<R> read(Function<AdcChannel, R> function) {
		return asList(leftAdc, rightAdc).stream().flatMap(adc -> adc.doWithChannels(function).stream()).collect(Collectors.toList());
	}

	protected void initialize() {
		asList(leftAdc, rightAdc).stream().forEach(adc -> {
			BitSet bitSet = new BitSet(8);
			bitSet.set(7);
			adc.write(adc.getAdcConfiguration().configure(bitSet));
		});
		initialized = true;
	}

	public void setLeftAdc(Adc leftAdc) {
		this.leftAdc = leftAdc;
	}

	public void setRightAdc(Adc rightAdc) {
		this.rightAdc = rightAdc;
	}

	public I2CBus getBus() {
		return bus;
	}
}
