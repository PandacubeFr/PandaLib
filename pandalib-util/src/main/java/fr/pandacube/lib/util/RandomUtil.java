package fr.pandacube.lib.util;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomUtil {

	public static final Random rand = new Random();

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
	 * @param set the Set from which to pick a random value
	 * @return a random value from the set
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
	 * @param f the frequencies of each entries
	 * @return the index of an entry, or -1 if it is unable to pick anything (all the frequencies are 0 or there is not provided frequency)
	 */
	public static int randomIndexOfFrequencies(double... f) {
		if (f == null)
			throw new IllegalArgumentException("f cannot be null");
		int n = f.length;
		double[] fSums = new double[n];
		double sum = 0;
		for (int i = 0; i < n; i++) {
			if (f[i] < 0)
				throw new IllegalArgumentException("f[" + i + "] cannot be negative.");
			fSums[i] = (sum += f[i]);
		}
		double r = rand.nextDouble() * sum;
		for (int i = 0; i < n; i++) {
			if (fSums[i] > r)
				return i;
		}
		return n - 1;
	}
	
	
	
	
	

	public static final String PASSWORD_CHARSET_LATIN_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
	public static final String PASSWORD_CHARSET_LATIN_UPPERCASE = PASSWORD_CHARSET_LATIN_LOWERCASE.toUpperCase();
	public static final String PASSWORD_CHARSET_DIGIT = "0123456789";
	public static final String PASSWORD_CHARSET_SPECIAL = "@#+*/-;:,.?!='()[]{}&";
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
