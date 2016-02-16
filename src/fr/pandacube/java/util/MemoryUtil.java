package fr.pandacube.java.util;

import java.text.DecimalFormat;

public class MemoryUtil {
	public static String humanReadableSize(long octet)
	{
		DecimalFormat format = new DecimalFormat("#####0.00");
		double size = octet;
		if (size < 1024)
			return size+"o";
		size /= 1024;
		if (size < 1024)
			return format.format(size)+"kio";
		size /= 1024;
		if (size < 1024)
			return format.format(size)+"Mio";
		size /= 1024;
		if (size < 1024)
			return format.format(size)+"Gio";
		size /= 1024;
		
		return format.format(size)+"Tio";
	}
}
