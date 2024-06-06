package fr.pandacube.lib.util;

import java.util.List;

/**
 * Provides utility methods for lists.
 */
public class ListUtil {

	/**
	 * Utility method to add to the provided list all the values in the provided interval.
	 * <p>
	 * If {@code min > max}, the list is not modified.
	 *
	 * @param list the list to add the values in.
	 * @param min the inclusive min value.
	 * @param max the inclusive max value.
	 */
	public static void addLongRangeToList(List<Long> list, long min, long max) {
		for (long i = min; i <= max; i++) {
			list.add(i);
		}
	}


	private ListUtil() {}

}
