package fr.pandacube.lib.core.util;

import java.text.DecimalFormat;

public class MemoryUtil {
	
	public enum MemoryUnit {
		B(1, 1, null),
		KB(1024, 1000, "k"),
		MB(1024 * 1024, 1000_000, "M"),
		GB(1024 * 1024 * 1024, 1000_000_000, "G");

		final long valueTrad;
		final long valueSI;
		final String unitMultiplier;
		
		public long toUnitRound(long byteCount, boolean si) {
			return byteCount / value(si);
		}
		
		public double toUnit(long byteCount, boolean si) {
			return byteCount / (double)value(si);
		}
		
		public long value(boolean si) {
			return si ? valueSI : valueTrad;
		}
		
		public String unit(boolean si) {
			return unitMultiplier == null ? "o" : (unitMultiplier + (si ? "o" : "io"));
		}
		
		MemoryUnit(long vTrad, long vSI, String uMult) {
			valueTrad = vTrad;
			valueSI = vSI;
			unitMultiplier = uMult;
		}
	}

	private static final DecimalFormat format = new DecimalFormat("#####0.00");

	public static String humanReadableSize(long octet, MemoryUnit roundTo, boolean si) {
		
		boolean neg = octet < 0;

		long size = Math.abs(octet);
		
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
			dispValue = ""+unit.toUnitRound(size, si);
		}
		else {
			dispValue = format.format(unit.toUnit(size, si));
		}
		
		return (neg ? "-" : "") + dispValue + unit.unit(si);
	}

	public static String humanReadableSize(long octet) {
		return humanReadableSize(octet, MemoryUnit.B, false);
	}
	
}
