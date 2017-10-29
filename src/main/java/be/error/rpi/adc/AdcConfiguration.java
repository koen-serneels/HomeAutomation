package be.error.rpi.adc;

import java.util.BitSet;

/**
 * @author Koen Serneels
 */
public class AdcConfiguration {

	public int address;

	public AdcConfiguration(int address) {
		this.address = address;
	}

	public BitSet configure(BitSet bitSet) {
		setGain1(bitSet);
		setBitRate12(bitSet);
		setContinousOn(bitSet);
		return bitSet;
	}

	public BitSet setGain1(BitSet bitSet) {
		bitSet.set(0, false);
		bitSet.set(1, false);
		return bitSet;
	}

	public BitSet setBitRate12(BitSet bitSet) {
		bitSet.set(2, false);
		bitSet.set(3, false);
		return bitSet;
	}

	public BitSet setContinousOn(BitSet bitSet) {
		bitSet.set(4, true);
		return bitSet;
	}

	public int getAddress() {
		return address;
	}
}



