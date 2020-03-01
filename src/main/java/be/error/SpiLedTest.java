package be.error;
/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Examples
 * FILENAME      :  SpiExample.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://www.pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2019 Pi4J
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;

import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;

/**
 * This example code demonstrates how to perform basic SPI communications using the Raspberry Pi. CS0 and CS1 (ship-select) are supported for SPI0.
 *
 * @author Robert Savage
 */
public class SpiLedTest {

	// SPI device
	public static SpiDevice spi = null;

	/**
	 * Sample SPI Program
	 *
	 * @param args (none) // (This is a utility class to abstract some of the boilerplate code) protected static final Console console = new Console(); };
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void main(String args[]) throws InterruptedException, IOException {

		int nrleds = 10;
		spi = SpiFactory.getInstance(SpiChannel.CS0, 3000000, // default spi speed 1 MHz
				SpiDevice.DEFAULT_SPI_MODE); // default spi mode 0

		BitSet bytes = new BitSet(7200);
		int i = 0;
		boolean forward = true;
		while (true) {

			if (i == 300) {
				forward = false;
			}

			i = forward ? ++i : --i;

			if (i == 0) {
				forward = true;
			}

			if (forward && i > nrleds) {
				int previous = ((i - (nrleds + 1)) * 24);
				bytes.set(previous, previous + 24, false);
			}
			if (!forward && i < (300 - nrleds)) {
				int previous = ((i + (nrleds + 1)) * 24);
				bytes.set(previous, previous + 24, false);
			}

			int current = i * 24;
			bytes.set(current, current + 8, true);

			spi.write(Arrays.copyOf(bytes.toByteArray(), 900));
			Thread.sleep(1);
		}

		//spi.write(new byte[] { (byte) 0, (byte) 0, (byte) 0 });

		// create Pi4J console wrapper/helper byte[] result = spi.write(data);
	}
}
