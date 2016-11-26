package fr.pandacube.java.util.network.packet.bytebuffer;

import java.util.Arrays;

public class Array8Bit {
	
	public static final int BIT_COUNT = 8;
	
	private boolean[] values = new boolean[BIT_COUNT];
	
	/**
	 * 
	 * @param b unsigned integer value. Lowest significant bit will be used.
	 */
	public Array8Bit(int b) {
		for (int i = 0; i<BIT_COUNT; i++) {
			values[i] = (b % 2 == 1);
			b >>= 1;
		}
	}
	
	
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
	public boolean getValue(int i) {
		return values[i];
	}
	
	/**
	 * i = 0 is the lowest significant bit
	 * @param i
	 * @param b
	 */
	public void setValue(int i, boolean b) {
		values[i] = b;
	}
	
	
	public byte getValuesAsByte() {
		byte b = 0;
		for (int i=BIT_COUNT-1; i>=0; i--) {
			b <<= 1;
			if (values[i]) b |= 1;
		}
		return b;
	}
	
}
