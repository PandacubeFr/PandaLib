package fr.pandacube.java.util;

import java.util.List;
import java.util.Random;

public class RandomUtil {

	public static Random rand = new Random();

	public static int nextIntBetween(int minInclu, int maxExclu) {
		return rand.nextInt(maxExclu - minInclu) + minInclu;
	}

	public static double nextDoubleBetween(double minInclu, double maxExclu) {
		return rand.nextDouble() * (maxExclu - minInclu) + minInclu;
	}
	
	public static <T> T arrayElement(T[] arr) {
		return arr[rand.nextInt(arr.length)];
	}
	
	public static <T> T listElement(List<T> arr) {
		return arr.get(rand.nextInt(arr.size()));
	}

}
