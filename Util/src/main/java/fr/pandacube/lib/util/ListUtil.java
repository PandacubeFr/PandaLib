package fr.pandacube.lib.util;

import java.util.List;

public class ListUtil {

	
	public static void addLongRangeToList(List<Long> list, long min, long max) {
		for (long i = min; i <= max; i++) {
			list.add(i);
		}
	}
}
