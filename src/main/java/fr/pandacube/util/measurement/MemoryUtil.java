package fr.pandacube.util.measurement;

import java.text.DecimalFormat;

public class MemoryUtil {

	private static final DecimalFormat format = new DecimalFormat("#####0.00");

	public static String humanReadableSize(long octet, boolean si) {
		
		boolean neg = octet < 0;

		double size = Math.abs(octet);

		int diveBy = si ? 1000 : 1024;

		if (size < diveBy) return (neg ? "-" : "") + size + "o";
		size /= diveBy;
		if (size < diveBy) return (neg ? "-" : "") + format.format(size) + (si ? "ko" : "kio");
		size /= diveBy;
		if (size < diveBy) return (neg ? "-" : "") + format.format(size) + (si ? "Mo" : "Mio");
		size /= diveBy;
		if (size < diveBy) return (neg ? "-" : "") + format.format(size) + (si ? "Go" : "Gio");
		size /= diveBy;

		return (neg ? "-" : "") + format.format(size) + (si ? "To" : "Tio");
	}

	public static String humanReadableSize(long octet) {
		return humanReadableSize(octet, false);
	}
}
