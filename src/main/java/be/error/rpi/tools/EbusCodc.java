/*-
 * #%L
 * Home Automation
 * %%
 * Copyright (C) 2016 - 2017 Koen Serneels
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package be.error.rpi.tools;

import java.math.BigDecimal;

/**
 * Created by koen on 12.04.17.
 */
public class EbusCodc {

	public static void main(String[] args) {
		System.err.println(decode("data1b", new byte[] { 0x5, 0x0 }, null));
	}

	/**
	 * Decodes a byte buffer to a number value
	 *
	 * @param type
	 * @param data
	 * @param replaceValue
	 * @return
	 */
	public static Number decode(String type, byte[] data, Number replaceValue) {

		Number n = null;
		Number repVal = 0;

		if (type.equals("data1b")) {
			n = decodeDATA1b(data[0]);
			repVal = -128;
		} else if (type.equals("data1c")) {
			n = decodeDATA1c(data[0]);
			repVal = 255;
		} else if (type.equals("data2b")) {
			n = decodeDATA2b(data);
			repVal = -128;
		} else if (type.equals("data2c")) {
			n = decodeDATA2c(data);
			repVal = -2048;
		} else if (type.equals("word") || type.equals("uint")) {
			n = decodeWord(data);
			repVal = 65535;
		} else if (type.equals("char")) {
			n = encodeChar(data[0]);
			repVal = 255;
		} else if (type.equals("int")) {
			n = decodeInt(data);
			repVal = 32768;
		} else if (type.equals("byte") || type.equals("uchar")) {
			n = decodeUChar(data[0]);
			repVal = 255;
		} else if (type.equals("bcd")) {
			n = decodeBCD(data[0]);
			repVal = 255;
		} else {
			throw new RuntimeException("Unable to encode data type \"" + type + "\", maybe currently not supported ...");
		}

		// if replace value parameter set
		if (replaceValue != null) {
			repVal = replaceValue;
		}

		BigDecimal b = NumberUtils.toBigDecimal(n);
		BigDecimal c = NumberUtils.toBigDecimal(repVal);

		// equals replace value, than return null
		if (b != null && c != null && b.compareTo(c) == 0) {

			n = null;
		}

		return n;
	}

	/**
	 * Encodes a number value to a byte buffer
	 *
	 * @param type
	 * @param value
	 * @return
	 */
	public static byte[] encode(String type, Object value) {

		if (value instanceof Number) {

			Number n = (Number) value;

			if (type.equals("data1b")) {
				return new byte[] { encodeDATA1b(n.byteValue()) };
			} else if (type.equals("data1c")) {
				return new byte[] { encodeDATA1c(n.floatValue()) };
			} else if (type.equals("data2b")) {
				return encodeDATA2b(n.floatValue());
			} else if (type.equals("data2c")) {
				return encodeDATA2c(n.floatValue());
			} else if (type.equals("word") || type.equals("uint")) {
				return encodeWord(n.shortValue());
			} else if (type.equals("int")) {
				return encodeInt(n.shortValue());
			} else if (type.equals("byte") || type.equals("uchar")) {
				return new byte[] { encodeUChar(n.byteValue()) };
			} else if (type.equals("char")) {
				return new byte[] { encodeChar(n.byteValue()) };
			} else if (type.equals("bcd")) {
				return new byte[] { encodeBCD(n.byteValue()) };
			}
		}

		throw new RuntimeException("Unable to encode data type " + type + ", maybe currently not supported ...");
	}

	/**
	 * Convert the value to a bcd value
	 *
	 * @param data The encoded value
	 * @return The bcd value
	 */
	public static byte decodeBCD(byte data) {
		return (byte) ((data >> 4) * 10 + (data & (byte) 0x0F));
	}

	/**
	 * Convert eBus Type CHAR
	 *
	 * @param data
	 * @return The decoded value
	 */
	public static short decodeChar(byte data) {
		return data;
	}

	/**
	 * Convert eBus Type DATA1B
	 *
	 * @param data The encoded value
	 * @return The decoded value
	 */
	public static short decodeDATA1b(byte data) {
		return decodeChar(data);
	}

	/**
	 * Convert eBus Type DATA1C
	 *
	 * @param data The encoded value
	 * @return The decoded value
	 */
	public static float decodeDATA1c(byte data) {
		return decodeUChar(data) / 2;
	}

	/**
	 * Convert eBus Type DATA2b
	 *
	 * @param data The encoded bytes
	 * @return The decoded value
	 */
	public static float decodeDATA2b(byte[] data) {
		return decodeInt(data) / 256f;
	}

	/**
	 * Convert eBus Type DATA2c
	 *
	 * @param data The encoded bytes
	 * @return The decoded value
	 */
	public static float decodeDATA2c(byte[] data) {
		return decodeInt(data) / 16f;
	}

	/**
	 * Convert eBus Type Int
	 *
	 * @param data The encoded bytes
	 * @return The decoded value
	 */
	public static short decodeInt(byte[] data) {
		return (short) (data[0] << 8 | data[1] & 0xFF);
	}

	/**
	 * Convert eBus Type UCHAR
	 *
	 * @param data
	 * @return The decoded value
	 */
	public static short decodeUChar(byte data) {
		return (short) (data & 0xFF);
	}

	/**
	 * Convert eBus Type WORD
	 *
	 * @param The encoded bytes
	 * @return The decoded value
	 */
	public static short decodeWord(byte[] data) {
		return (short) (decodeInt(data) & 0xffff);
	}

	/**
	 * Encodes a BCD value
	 *
	 * @param data
	 * @return
	 */
	public static byte encodeBCD(byte data) {
		return (byte) ((byte) ((byte) (data / 10) << 4) | data % 10);
	}

	/**
	 * Encodes a CHAR value
	 *
	 * @param data
	 * @return
	 */
	public static byte encodeChar(byte data) {
		return data;
	}

	/**
	 * Encodes a DATA1b value
	 *
	 * @param data
	 * @return
	 */
	public static byte encodeDATA1b(byte data) {
		return encodeChar(data);
	}

	/**
	 * Encodes a DATA1c value
	 *
	 * @param data
	 * @return
	 */
	public static byte encodeDATA1c(float data) {
		return encodeUChar((short) (data * 2));
	}

	/**
	 * Encodes a DATA2b value
	 *
	 * @param data
	 * @return
	 */
	public static byte[] encodeDATA2b(float data) {
		return encodeInt((short) (data * 256));
	}

	/**
	 * Encodes a DATA2c value
	 *
	 * @param data
	 * @return
	 */
	public static byte[] encodeDATA2c(float data) {
		return encodeInt((short) (data * 16));
	}

	/**
	 * Encodes a INT value
	 *
	 * @param data
	 * @return
	 */
	public static byte[] encodeInt(short data) {
		return new byte[] { (byte) (data >> 8), (byte) data };
	}

	/**
	 * Encodes a UCHAR value
	 *
	 * @param data
	 * @return
	 */
	public static byte encodeUChar(short data) {
		return (byte) (data & 0xFF);
	}

	/**
	 * Encodes a WORD value
	 *
	 * @param data
	 * @return
	 */
	public static byte[] encodeWord(short data) {
		return encodeInt((short) (data & 0xFFFF));
	}
}
