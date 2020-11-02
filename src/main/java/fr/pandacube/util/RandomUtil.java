package fr.pandacube.util;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomUtil {

	public static Random rand = new Random();

	public static int nextIntBetween(int minInclu, int maxExclu) {
		return rand.nextInt(maxExclu - minInclu) + minInclu;
	}

	public static double nextDoubleBetween(double minInclu, double maxExclu) {
		return rand.nextDouble() * (maxExclu - minInclu) + minInclu;
	}
	
	public static <T> T arrayElement(T[] arr) {
		return (arr == null || arr.length == 0) ? null : arr[rand.nextInt(arr.length)];
	}
	
	public static <T> T listElement(List<T> arr) {
		return (arr == null || arr.isEmpty()) ? null : arr.get(rand.nextInt(arr.size()));
	}
	
	/**
	 * Returns a random value from a set.
	 * 
	 * May not be optimized (Actually O(n) )
	 * @param arr
	 * @return
	 */
	public static <T> T setElement(Set<T> set) {
		if (set.isEmpty())
			throw new IllegalArgumentException("set is empty");
		int retI = rand.nextInt(set.size()), i = 0;
		for (T e : set) {
			if (retI == i)
				return e;
			i++;
		}
		throw new RuntimeException("Should never go to this line of code");
	}

}
