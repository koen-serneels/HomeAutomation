package be.error.rpi.heating;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoomTemperatureDeltaSorter {

	private static final Logger logger = LoggerFactory.getLogger("heating");

	/**
	 * This descending sorting results in positives delta's (current temp is lower than desired, they require heating) sorted highest first: the room with the
	 * most heating demand is first positioned in the list. Negative or zero delta's (current temp is higher or equal to desired temp, they don't require heating) are
	 * sorted after the positive delta's (if any) but lowest first: the room with its current temp the closest to it's desired temp (but equal or higher then it's
	 * desired temp) will be first.
	 *
	 * For the positive delta's this makes sense as the one with the largest delta should be send to the heater. If no positive delta's exists, but there are zero or
	 * negatives, we send the zero or negative instead. In this case it makes sense to send the delta that will be the closest to a potential heating demand in the near
	 * future (=the least negative delta)
	 */
	public List<RoomTemperature> sortRoomTemperatureInfos(List<RoomTemperature> roomTemperatureInfos) {
		List<RoomTemperature> list = roomTemperatureInfos.stream().sorted((l, r) -> r.delta().compareTo(l.delta())).collect(toList());

		logger.debug("Sorted room temperature list:" + list.toString());

		return list;
	}
}
