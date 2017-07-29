package be.error.rpi.adc;

import static be.error.rpi.adc.ObjectStatusTypeMapper.ObjectStatusType.CIRCUIT_BREACH;
import static be.error.rpi.adc.ObjectStatusTypeMapper.ObjectStatusType.CLOSED;
import static be.error.rpi.adc.ObjectStatusTypeMapper.ObjectStatusType.OPEN;
import static be.error.rpi.adc.ObjectStatusTypeMapper.ObjectStatusType.SHORT_CIRCUIT;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Koen Serneels
 */
public class ObjectStatusTypeMapper {

	private List<ObjectStatusTypeMapping> mapping = new ArrayList() {
		{
			add(new ObjectStatusTypeMapping(SHORT_CIRCUIT, "0.00", "4.00"));
			add(new ObjectStatusTypeMapping(OPEN, "4.20", "4.40"));
			add(new ObjectStatusTypeMapping(CLOSED, "4.41", "4.65"));
			add(new ObjectStatusTypeMapping(CIRCUIT_BREACH, "4.70", null));
		}
	};

	public ObjectStatusType map(BigDecimal value) {
		return mapping.stream().filter(mapping -> mapping.match(value)).findFirst()
				.orElseThrow(() -> new IllegalStateException("Value " + value + " had no object status mapping. Available mappings:" + mapping)).getObjectStatusType();
	}

	private class ObjectStatusTypeMapping {

		private final ObjectStatusType objectStatusType;
		private final BigDecimal lower, upper;

		ObjectStatusTypeMapping(ObjectStatusType objectStatusType, String lower, String upper) {
			this.objectStatusType = objectStatusType;
			this.lower = isBlank(lower) ? null : new BigDecimal(lower);
			this.upper = isBlank(upper) ? null : new BigDecimal(upper);
		}

		public boolean match(BigDecimal value) {
			return (lower == null || value.compareTo(lower) >= 0) && (upper == null || value.compareTo(upper) <= 0);
		}

		public ObjectStatusType getObjectStatusType() {
			return objectStatusType;
		}

		@Override
		public String toString() {
			return "[" + objectStatusType + " Lower:" + lower + " Upper:" + upper + "]";
		}
	}

	public enum ObjectStatusType {
		OPEN((byte) 1),
		CLOSED((byte) 2),
		SHORT_CIRCUIT((byte) 3),
		CIRCUIT_BREACH((byte) 4),
		READ_ERROR((byte) 5);

		private byte id;

		ObjectStatusType(final byte id) {
			this.id = id;
		}

		public byte getId() {
			return id;
		}
	}
}
