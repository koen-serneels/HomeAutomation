package be.error.rpi.heating;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import be.error.rpi.heating.HeatingController.RoomTemperatureInfo;
import be.error.types.LocationId;

@Test
public class RoomTemperatureDeltaSorterTest {

	private RoomTemperatureDeltaSorter sorter = new RoomTemperatureDeltaSorter();

	public void test() {
		List<RoomTemperatureInfo> roomTemperatureInfos = new ArrayList<>();
		roomTemperatureInfos.add(create("20.00", "21.00"));
		roomTemperatureInfos.add(create("19.00", "21.00"));
		roomTemperatureInfos.add(create("18.99", "21.00"));
		roomTemperatureInfos.add(create("20.00", "21.00"));

		List<RoomTemperatureInfo> sorted = sorter.sortRoomTemperatureInfos(roomTemperatureInfos);
		assertEquals(sorted.get(0).getRoomTemperature(), create("18.99", "21.00").getRoomTemperature());
		assertEquals(sorted.get(1).getRoomTemperature(), create("19.00", "21.00").getRoomTemperature());
		assertEquals(sorted.get(2).getRoomTemperature(), create("20.00", "21.00").getRoomTemperature());
		assertEquals(sorted.get(3).getRoomTemperature(), create("20.00", "21.00").getRoomTemperature());

		roomTemperatureInfos = new ArrayList<>();
		roomTemperatureInfos.add(create("20.00", "21.00"));
		roomTemperatureInfos.add(create("19.00", "21.00"));
		roomTemperatureInfos.add(create("27.00", "21.00"));
		roomTemperatureInfos.add(create("25.00", "21.00"));
		roomTemperatureInfos.add(create("18.99", "21.00"));
		roomTemperatureInfos.add(create("20.00", "21.00"));
		roomTemperatureInfos.add(create("24.00", "21.00"));

		sorted = sorter.sortRoomTemperatureInfos(roomTemperatureInfos);
		assertEquals(sorted.get(0).getRoomTemperature(), create("18.99", "21.00").getRoomTemperature());
		assertEquals(sorted.get(1).getRoomTemperature(), create("19.00", "21.00").getRoomTemperature());
		assertEquals(sorted.get(2).getRoomTemperature(), create("20.00", "21.00").getRoomTemperature());
		assertEquals(sorted.get(3).getRoomTemperature(), create("20.00", "21.00").getRoomTemperature());
		assertEquals(sorted.get(4).getRoomTemperature(), create("24.00", "21.00").getRoomTemperature());
		assertEquals(sorted.get(5).getRoomTemperature(), create("25.00", "21.00").getRoomTemperature());
		assertEquals(sorted.get(6).getRoomTemperature(), create("27.00", "21.00").getRoomTemperature());

		roomTemperatureInfos = new ArrayList<>();
		roomTemperatureInfos.add(create("20.00", "21.00"));
		roomTemperatureInfos.add(create("19.00", "21.00"));
		roomTemperatureInfos.add(create("27.00", "21.00"));
		roomTemperatureInfos.add(create("25.00", "21.00"));
		roomTemperatureInfos.add(create("18.99", "21.00"));
		roomTemperatureInfos.add(create("20.00", "21.00"));
		roomTemperatureInfos.add(create("21.00", "21.00"));
		roomTemperatureInfos.add(create("24.00", "21.00"));

		sorted = sorter.sortRoomTemperatureInfos(roomTemperatureInfos);
		assertEquals(sorted.get(0).getRoomTemperature(), create("18.99", "21.00").getRoomTemperature());
		assertEquals(sorted.get(1).getRoomTemperature(), create("19.00", "21.00").getRoomTemperature());
		assertEquals(sorted.get(2).getRoomTemperature(), create("20.00", "21.00").getRoomTemperature());
		assertEquals(sorted.get(3).getRoomTemperature(), create("20.00", "21.00").getRoomTemperature());
		assertEquals(sorted.get(4).getRoomTemperature(), create("21.00", "21.00").getRoomTemperature());
		assertEquals(sorted.get(5).getRoomTemperature(), create("24.00", "21.00").getRoomTemperature());
		assertEquals(sorted.get(6).getRoomTemperature(), create("25.00", "21.00").getRoomTemperature());
		assertEquals(sorted.get(7).getRoomTemperature(), create("27.00", "21.00").getRoomTemperature());
	}

	private RoomTemperatureInfo create(String current, String desired) {
		RoomTemperature roomTemperature = new RoomTemperature(LocationId.BADKAMER, new BigDecimal(current).setScale(2), new BigDecimal(desired).setScale(2));
		RoomTemperatureInfo roomTemperatureInfo = new RoomTemperatureInfo(roomTemperature, mock(RoomTemperatureProcessor.class));
		return roomTemperatureInfo;
	}
}