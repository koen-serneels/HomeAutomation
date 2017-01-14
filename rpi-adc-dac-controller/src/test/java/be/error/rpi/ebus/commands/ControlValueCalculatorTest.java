package be.error.rpi.ebus.commands;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;

import org.testng.annotations.Test;

import be.error.rpi.ebus.EbusdTcpCommunicator;

@Test
public class ControlValueCalculatorTest {

	public void test() throws Exception {

		ControlValueCalculator controlValueCalculator = new ControlValueCalculator(mock(EbusdTcpCommunicator.class));

		assertEquals(controlValueCalculator.decide(new BigDecimal("22.50"), new BigDecimal("23.00")), new BigDecimal("22.50"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("22.60"), new BigDecimal("23.00")), new BigDecimal("22.50"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("22.70"), new BigDecimal("23.00")), new BigDecimal("22.50"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("22.80"), new BigDecimal("23.00")), new BigDecimal("22.80"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("22.90"), new BigDecimal("23.00")), new BigDecimal("22.90"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("22.95"), new BigDecimal("23.00")), new BigDecimal("22.90"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("23.00"), new BigDecimal("23.00")), new BigDecimal("22.90"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("23.05"), new BigDecimal("23.00")), new BigDecimal("22.90"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("23.10"), new BigDecimal("23.00")), new BigDecimal("22.90"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("23.16"), new BigDecimal("23.00")), new BigDecimal("23.16"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("23.30"), new BigDecimal("23.00")), new BigDecimal("23.30"));

		assertEquals(controlValueCalculator.decide(new BigDecimal("22.50"), new BigDecimal("23.40")), new BigDecimal("22.50"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("22.60"), new BigDecimal("23.40")), new BigDecimal("22.60"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("22.70"), new BigDecimal("23.40")), new BigDecimal("22.70"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("22.80"), new BigDecimal("23.40")), new BigDecimal("22.80"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("22.90"), new BigDecimal("23.40")), new BigDecimal("22.90"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("22.95"), new BigDecimal("23.40")), new BigDecimal("22.90"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("23.00"), new BigDecimal("23.40")), new BigDecimal("22.90"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("23.05"), new BigDecimal("23.40")), new BigDecimal("22.90"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("23.10"), new BigDecimal("23.40")), new BigDecimal("22.90"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("23.16"), new BigDecimal("23.40")), new BigDecimal("22.90"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("23.20"), new BigDecimal("23.40")), new BigDecimal("23.20"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("23.29"), new BigDecimal("23.40")), new BigDecimal("23.29"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("23.35"), new BigDecimal("23.40")), new BigDecimal("23.30"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("23.45"), new BigDecimal("23.40")), new BigDecimal("23.30"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("23.50"), new BigDecimal("23.40")), new BigDecimal("23.30"));
		assertEquals(controlValueCalculator.decide(new BigDecimal("23.51"), new BigDecimal("23.40")), new BigDecimal("23.51"));
	}
}



