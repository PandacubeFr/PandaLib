package fr.pandacube.lib.util;

import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Utility class to generate random things.
 */
public class RandomUtil {

	/**
	 * The unique {@link Random} instance used in this class. Can also be used else where when needed.
	 */
	public static final Random rand = new Random();

	/**
	 * Returns a randomly generated integer between {@code minInclu} included and {@code maxExclu} excluded.
	 * This method is safer to use that Random#nextInt(int, int) because it does not check the validity of
	 * the parameters.
	 * @param minInclu the minimum value, included.
	 * @param maxExclu the maximum value, excluded.
	 * @return a random number between {@code minInclu} included and {@code maxExclu} excluded.
	 * @see Random#nextInt(int, int)
	 * @throws IllegalArgumentException if {@code minInclu} is greater than {@code maxExclu}.
	 */
	public static int nextIntBetween(int minInclu, int maxExclu) {
		return minInclu + rand.nextInt(maxExclu - minInclu);
	}

	/**
	 * Returns a randomly generated double between {@code minInclu} included and {@code maxExclu} excluded.
	 * This method is safer to use that Random#nextDouble(double, double) because it does not check the validity of
	 * the parameters
	 * @param minInclu the minimum value, included.
	 * @param maxExclu the maximum value, excluded.
	 * @return a random number between {@code minInclu} included and {@code maxExclu} excluded.
	 * @see Random#nextDouble(double, double)
	 */
	public static double nextDoubleBetween(double minInclu, double maxExclu) {
		return minInclu + rand.nextDouble() * (maxExclu - minInclu);
	}

	/**
	 * Returns a random element from the provided array.
	 * @param array the array in whith to pick a value randomly.
	 * @return the value randomly picked from the array, or null if the array is null or empty.
	 * @param <T> the type of the array elements.
	 * @see Random#nextInt(int)
	 */
	public static <T> T arrayElement(T[] array) {
		return (array == null || array.length == 0) ? null : array[rand.nextInt(array.length)];
	}

	/**
	 * Returns a random element from the provided list.
	 * @param list the list in whith to pick a value randomly.
	 * @return the value randomly picked from the list, or null if the array is null or empty.
	 * @param <T> the type of the list elements.
	 * @see Random#nextInt(int)
	 */
	public static <T> T listElement(List<T> list) {
		return (list == null || list.isEmpty()) ? null : list.get(rand.nextInt(list.size()));
	}

	/**
	 * Returns a random character from the provided string.
	 * @param str the string in whith to pick a character randomly.
	 * @return the character randomly picked from the string, or {@code '\0'} if the string is null or empty.
	 * @see Random#nextInt(int)
	 */
	public static char stringChar(String str) {
		return (str == null || str.isEmpty()) ? '\0' : str.charAt(rand.nextInt(str.length()));
	}
	
	/**
	 * Returns a random value from the provided set.
	 * @param set the set in which to pick a value randomly.
	 * @return the value randomly picked from the list, or null if the set is null or empty.
	 * @param <T> the type of the set elements.
	 * @implNote The current implementation uses the iterator of the set to pick a random value, since there is no way
	 * to directly pick a value using an index.
	 * @throws IllegalStateException if the set has reduced in size during the execution of this method, making the
	 * iterator reaching the end of the set before getting the value the random generator picked.
	 */
	public static <T> T setElement(Set<T> set) {
		if (set == null || set.isEmpty())
			return null;
		int retI = rand.nextInt(set.size()), i = 0;
		for (T e : set) {
			if (retI == i)
				return e;
			i++;
		}
		throw new IllegalStateException("Should never go to this line of code");
	}
	
	/**
	 * Return a value between 0 and the number of parameter minus 1, using the provided frequencies.
	 * <p>
	 * The probability of each value to be returned depends of the frequencies provided.
	 * @param frequencies the frequencies of each entries
	 * @return the index of an entry, or -1 if it is unable to pick anything (all the frequencies are 0 or there is no provided frequency)
	 * @throws IllegalArgumentException if frequencies is null.
	 */
	public static int randomIndexOfFrequencies(double... frequencies) {
		if (frequencies == null)
			throw new IllegalArgumentException("frequencies cannot be null");
		int n = frequencies.length;
		double[] fSums = new double[n];
		double sum = 0;
		for (int i = 0; i < n; i++) {
			if (frequencies[i] < 0)
				throw new IllegalArgumentException("frequencies[" + i + "] cannot be negative.");
			fSums[i] = (sum += frequencies[i]);
		}
		double r = rand.nextDouble() * sum;
		for (int i = 0; i < n; i++) {
			if (fSums[i] > r)
				return i;
		}
		return n - 1;
	}


	/**
	 * A set of characters representing all the lowercase letters of the latin alphabet (only in the ASCII table).
	 */
	public static final String PASSWORD_CHARSET_LATIN_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";

	/**
	 * A set of characters representing all the uppercase letters of the latin alphabet (only in the ASCII table).
	 */
	public static final String PASSWORD_CHARSET_LATIN_UPPERCASE = PASSWORD_CHARSET_LATIN_LOWERCASE.toUpperCase();

	/**
	 * A set of characters representing all the number digits, from 0 to 9.
	 */
	public static final String PASSWORD_CHARSET_DIGIT = "0123456789";

	/**
	 * A set of characters representing some visible special characters in the ASCII table.
	 */
	public static final String PASSWORD_CHARSET_SPECIAL = "@#+*/-;:,.?!='()[]{}&";

	/**
	 * A set of characters representing uppercase and lowercase latin alphabet letters and digits, exclusing some that
	 * can be confusing to read (like {@code iIl1} or {@code oO0}).
	 */
	public static final String PASSWORD_CHARSET_NO_ANBIGUITY = "abcdefghkmnpqrstwxyzACDEFGHKLMNPQRSTWXYZ2345679";

	/**
	 * Generate a random password of the provided length, using the characters listed in {@link #PASSWORD_CHARSET_NO_ANBIGUITY}.
	 * @param length the length of the generated password.
	 * @return the generated password.
	 */
	public static String randomPassword(int length) {
		return randomPassword(length, PASSWORD_CHARSET_NO_ANBIGUITY);
	}

	/**
	 * Generate a random password of the provided length, using the provided characters in a string.
	 * @param length the length of the generated password.
	 * @param charset the characters to use. It’s possible to use of the {@code PASSWORD_*} static strings in this class.
	 * @return the generated password.
	 */
	public static String randomPassword(int length, String charset) {
		char[] pw = new char[length];
		for (int i = 0; i < length; i++) {
			pw[i] = stringChar(charset);
		}
		return String.valueOf(pw);
	}
	
	
	

}
