package fr.pandacube.lib.util;

import java.text.DecimalFormat;

/**
 * This class contains various methods to manipulate and display memory measurements.
 */
public class MemoryUtil {

	private static final DecimalFormat format = new DecimalFormat("#####0.00");

	/**
	 * Generate a string representation of the provided memory amount, using the provided memory unit and either to use
	 * SI or traditional units.
	 * <p>
	 * <b>This method returns the unit symbol in French.</b>
	 * @param bytes the memory amount to format.
	 * @param roundTo the memory unit to convert and display the amount to.
	 * @param si true to use SI unit, false to use traditional.
	 * @return a string representation of the provided memory amount.
	 */
	public static String humanReadableSize(long bytes, MemoryUnit roundTo, boolean si) {
		
		boolean neg = bytes < 0;

		long size = Math.abs(bytes);
		
		MemoryUnit unit = roundTo;
		for (int ui = MemoryUnit.values().length - 1; ui >= 0; ui--) {
			MemoryUnit u = MemoryUnit.values()[ui];
			if (u == roundTo)
				break;
			if (size < u.value(si))
				continue;
			unit = u;
			break;
		}
		
		String dispValue;
		if (unit == roundTo) {
			dispValue = "" + unit.toUnitRound(size, si);
		}
		else {
			dispValue = format.format(unit.toUnit(size, si));
		}
		
		return (neg ? "-" : "") + dispValue + unit.unit(si);
	}

	/**
	 * Generate a string representation of the provided memory amount, displayinh the value in byte (as is) and with the
	 * unit symbol {@code "o"}.
	 * <p>
	 * <b>This method returns the unit symbol in French.</b>
	 * @param bytes the memory amount to format.
	 * @return a string representation of the provided memory amount.
	 */
	public static String humanReadableSize(long bytes) {
		return humanReadableSize(bytes, MemoryUnit.B, false);
	}



	/**
	 * Enumeration of comonly used unit of memory prefix.
	 */
	public enum MemoryUnit {

		/**
		 * Byte unit.
		 * <p>
		 * SI unit: 10<sup>0</sup> = 1B<br>
		 * Traditional unit: 2<sup>0</sup> = 1B
		 */
		B(1, 1, null),

		/**
		 * Kilobyte unit.
		 * <p>
		 * SI unit: 10<sup>3</sup>B = 1000B = 1KB (SI unit)<br>
		 * Traditional unit: 2<sup>10</sup> = 1024B = 1KiB
		 */
		KB(1024, 1000, "k"),

		/**
		 * Megabyte unit.
		 * <p>
		 * SI unit: 10<sup>6</sup>B = 1000000B = 1MB (SI unit)<br>
		 * Traditional unit: 2<sup>20</sup> = 1048576B = 1MiB
		 */
		MB(1024 * 1024, 1000_000, "M"),

		/**
		 * Gigabyte unit.
		 * <p>
		 * SI unit: 10<sup>9</sup>B = 1000000000B = 1GB (SI unit)<br>
		 * Traditional unit: 2<sup>30</sup> = 1073741824B = 1GiB
		 */
		GB(1024 * 1024 * 1024, 1000_000_000, "G");

		/**
		 * The traditional (power of 2) value of this memory unit, in byte.
		 */
		public final long valueTrad;

		/**
		 * The SI standard (power of 10) value of this memory unit, in byte.
		 */
		public final long valueSI;

		/**
		 * The prefix symbol for this unit.
		 */
		public final String unitMultiplier;

		/**
		 * Converts the provided memory amount to this unit, rounded down (using integer division).
		 * @param byteCount the memory amount to convert.
		 * @param si true to use SI unit, false to use traditional.
		 * @return the converted value.
		 */
		public long toUnitRound(long byteCount, boolean si) {
			return byteCount / value(si);
		}

		/**
		 * Converts the provided memory amount to this unit.
		 * @param byteCount the memory amount to convert.
		 * @param si true to use SI unit, false to use traditional.
		 * @return the converted value.
		 */
		public double toUnit(long byteCount, boolean si) {
			return byteCount / (double)value(si);
		}

		/**
		 * The value of this memory unit, in byte, in either SI or traditional unit.
		 * @param si true to use SI unit, false to use traditional.
		 * @return value of this memory unit, in byte.
		 */
		public long value(boolean si) {
			return si ? valueSI : valueTrad;
		}

		/**
		 * Returns the full unit symbol of this unit, that is the prefix {@link #unitMultiplier} concatenated with
		 * either "o" for SI unit or "io" to traditional unit.
		 * @param si true to use SI unit, false to use traditional.
		 * @return the full unit symbol of this unit.
		 */
		public String unit(boolean si) {
			return unitMultiplier == null ? "o" : (unitMultiplier + (si ? "o" : "io"));
		}

		MemoryUnit(long vTrad, long vSI, String uMult) {
			valueTrad = vTrad;
			valueSI = vSI;
			unitMultiplier = uMult;
		}
	}
	
}
