package be.error.rpi.heating;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.math.BigDecimal;

import org.testng.annotations.Test;

import be.error.types.LocationId;

@Test
public class ControlValueCalculatorTest {

	public void testGetControlValue() throws Exception {

		ControlValueCalculator controlValueCalculator = new ControlValueCalculator();

		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("22.50"), new BigDecimal("23.00")), new BigDecimal("22.50"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("22.60"), new BigDecimal("23.00")), new BigDecimal("22.50"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("22.70"), new BigDecimal("23.00")), new BigDecimal("22.50"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("22.80"), new BigDecimal("23.00")), new BigDecimal("22.80"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("22.90"), new BigDecimal("23.00")), new BigDecimal("22.90"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("22.95"), new BigDecimal("23.00")), new BigDecimal("22.90"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("23.00"), new BigDecimal("23.00")), new BigDecimal("22.90"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("23.05"), new BigDecimal("23.00")), new BigDecimal("22.90"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("23.10"), new BigDecimal("23.00")), new BigDecimal("22.90"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("23.16"), new BigDecimal("23.00")), new BigDecimal("23.16"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("23.30"), new BigDecimal("23.00")), new BigDecimal("23.30"));

		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("22.50"), new BigDecimal("23.40")), new BigDecimal("22.50"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("22.60"), new BigDecimal("23.40")), new BigDecimal("22.60"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("22.70"), new BigDecimal("23.40")), new BigDecimal("22.70"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("22.80"), new BigDecimal("23.40")), new BigDecimal("22.80"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("22.90"), new BigDecimal("23.40")), new BigDecimal("22.90"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("22.95"), new BigDecimal("23.40")), new BigDecimal("22.90"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("23.00"), new BigDecimal("23.40")), new BigDecimal("22.90"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("23.05"), new BigDecimal("23.40")), new BigDecimal("22.90"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("23.10"), new BigDecimal("23.40")), new BigDecimal("22.90"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("23.16"), new BigDecimal("23.40")), new BigDecimal("22.90"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("23.20"), new BigDecimal("23.40")), new BigDecimal("23.20"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("23.29"), new BigDecimal("23.40")), new BigDecimal("23.29"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("23.35"), new BigDecimal("23.40")), new BigDecimal("23.30"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("23.45"), new BigDecimal("23.40")), new BigDecimal("23.30"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("23.50"), new BigDecimal("23.40")), new BigDecimal("23.30"));
		assertEquals(controlValueCalculator.getControlValue(new BigDecimal("23.51"), new BigDecimal("23.40")), new BigDecimal("23.51"));
	}

	public void testHasHeatingDemand() throws Exception {
		ControlValueCalculator controlValueCalculator = new ControlValueCalculator();

		assertTrue(controlValueCalculator.updateHeatingDemand(create("22.40", "23.00")).getHeatingDemand());
		assertTrue(controlValueCalculator.updateHeatingDemand(create("22.50", "23.00")).getHeatingDemand());
		assertTrue(controlValueCalculator.updateHeatingDemand(create("22.70", "23.00")).getHeatingDemand());
		assertTrue(controlValueCalculator.updateHeatingDemand(create("22.80", "23.00")).getHeatingDemand());
		assertTrue(controlValueCalculator.updateHeatingDemand(create("23.10", "23.00")).getHeatingDemand());
		assertFalse(controlValueCalculator.updateHeatingDemand(create("23.11", "23.00")).getHeatingDemand());

		RoomTemperature roomTemperature = controlValueCalculator.updateHeatingDemand(create("22.80", "23.00"));
		roomTemperature.updateHeatingDemand(false);
		assertFalse(controlValueCalculator.updateHeatingDemand(roomTemperature).getHeatingDemand());
		roomTemperature.updateHeatingDemand(true);
		assertTrue(controlValueCalculator.updateHeatingDemand(roomTemperature).getHeatingDemand());

		roomTemperature = controlValueCalculator.updateHeatingDemand(create("22.80", "23.00"));
		roomTemperature.updateHeatingDemand(false);
		assertFalse(controlValueCalculator.updateHeatingDemand(roomTemperature).getHeatingDemand());
		roomTemperature.updateCurrentTemp(22.50);
		assertTrue(controlValueCalculator.updateHeatingDemand(roomTemperature).getHeatingDemand());

		roomTemperature = controlValueCalculator.updateHeatingDemand(create("22.80", "23.00"));
		roomTemperature.updateHeatingDemand(true);
		assertTrue(controlValueCalculator.updateHeatingDemand(roomTemperature).getHeatingDemand());
		roomTemperature.updateCurrentTemp(23.00);
		assertTrue(controlValueCalculator.updateHeatingDemand(roomTemperature).getHeatingDemand());
		roomTemperature.updateCurrentTemp(23.12);
		assertFalse(controlValueCalculator.updateHeatingDemand(roomTemperature).getHeatingDemand());
	}

	private RoomTemperature create(String current, String desired) {
		RoomTemperature roomTemperature = new RoomTemperature(LocationId.BADKAMER, new BigDecimal(current).setScale(2), new BigDecimal(desired).setScale(2));
		return roomTemperature;
	}
}



