package fr.pandacube.lib.core.net;

import java.util.Arrays;

public class Array8Bit {
	
	public static final int BIT_COUNT = Byte.SIZE;
	
	private boolean[] values = new boolean[BIT_COUNT];
	
	public Array8Bit(byte b) {
		fromByte(b);
	}

	/**
	 * @param bits (index 0 is the lowest significant bit)
	 */
	public Array8Bit(boolean[] bits) {
		if (bits == null || bits.length != BIT_COUNT)
			throw new IllegalArgumentException("bits is null or bits.length != "+BIT_COUNT);
		values = Arrays.copyOf(bits, BIT_COUNT);
	}
	
	/**
	 * i = 0 is the lowest significant bit
	 * @param i
	 * @return
	 */
	public boolean getBit(int i) {
		return values[i];
	}
	
	/**
	 * i = 0 is the lowest significant bit
	 * @param i
	 * @param b
	 */
	public void setBit(int i, boolean b) {
		values[i] = b;
	}
	
	
	
	public void fromByte(byte b) {
		int mask = 1;
		for (int i = 0; i < BIT_COUNT; i++) {
			values[i] = (b & mask) != 0;
			mask <<= 1;
		}
	}
	
	
	
	public byte toByte() {
		byte b = 0;
		for (int i=BIT_COUNT-1; i>=0; i--) {
			b <<= 1;
			if (values[i]) b |= 1;
		}
		return b;
	}
	
	
	
	
}
