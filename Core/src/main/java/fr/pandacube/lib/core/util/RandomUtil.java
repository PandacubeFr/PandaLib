package fr.pandacube.lib.core.util;

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
	
	public static char stringChar(String arr) {
		return (arr == null || arr.isEmpty()) ? '\0' : arr.charAt(rand.nextInt(arr.length()));
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
	
	/**
	 * Return a value between 0 and the number of parameter minus 1, using the provided frequencies.
	 * 
	 * The probability of each value to be returned depends of the frequencies provided.
	 * @param frequencies the frequencies of each entries
	 * @return the index of an entry, or -1 if it is unable to pick anything (all the frequencies are 0 or there is not provided frequency)
	 */
	public static int randomIndexOfFrequencies(double... frequencies) {
		if (frequencies == null)
			return -1;
		double sum = 0;
		for (double f : frequencies)
			sum += f;
		if (sum == 0)
			return -1;
		double r = rand.nextDouble() * sum;
		int i = -1;
		double limit = frequencies[++i];
		while (i < frequencies.length) {
			if (r < limit)
				return i;
			limit += frequencies[++i];
		}
		return frequencies.length - 1;
	}
	
	
	
	
	

	public static final String PASSWORD_CHARSET_LATIN_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
	public static final String PASSWORD_CHARSET_LATIN_UPPERCASE = PASSWORD_CHARSET_LATIN_LOWERCASE.toUpperCase();
	public static final String PASSWORD_CHARSET_DIGIT = "0123456789";
	public static final String PASSWORD_CHARSET_SPECIAL = "@#+*/-;:,.?!='()[]{}&\"\\";
	public static final String PASSWORD_CHARSET_NO_ANBIGUITY = "abcdefghkmnpqrstwxyzACDEFGHKLMNPQRSTWXYZ2345679";

	public static String randomPassword(int length) {
		return randomPassword(length, PASSWORD_CHARSET_NO_ANBIGUITY);
	}
	
	public static String randomPassword(int length, String charset) {
		char[] pw = new char[length];
		for (int i = 0; i < length; i++) {
			pw[i] = stringChar(charset);
		}
		return String.valueOf(pw);
	}
	
	
	

}
