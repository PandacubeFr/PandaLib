package fr.pandacube.lib.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides various methods to manipulate Strings.
 */
public class StringUtil {

	/**
	 * Format the provided double, omitting the decimal part if the provided double is strictly equals to a long value.
	 * @param d the value to convert to string.
	 * @return a string representation of the double value.
	 */
	public static String formatDouble(double d) {
		if (d == (long) d)
			return String.format("%d", (long) d);
		return String.valueOf(d);
	}

	/**
	 * Counts the number of occurence of a speficied character in a string.
	 * @param string the character sequence to search into.
	 * @param character the character to count.
	 * @return the number of occurence of
	 * @deprecated Because it uses snake_case naming convention. Use {@link #countOccurences(CharSequence, char)} instead.
	 */
	@Deprecated(forRemoval = true, since = "2022-07-26")
	public static int char_count(CharSequence string, char character) {
		return countOccurences(string, character);
	}

	/**
	 * Counts the number of occurence of a speficied character in a string.
	 * @param string the character sequence to search into.
	 * @param character the character to count.
	 * @return the number of occurence of
	 */
	public static int countOccurences(CharSequence string, char character) {
		int count = 0;
		for (char c : string.toString().toCharArray()) {
			if (c == character) {
				count++;
			}
		}
		return count;
	}


	/**
	 * Do like {@link String#join(CharSequence, Iterable)}, but the last separator is different than the others.
	 * It is usedful when enumerating thins in a sentense, for instance : <code>"a thing<u>, </u>a thing<u> and </u>a thing"</code>
	 * (the coma being the usual separator, and {@code " and "} being the final separator).
	 * @param regularSeparator the separator used everywhere except between the two last strings to join.
	 * @param finalSeparator the separator used between the two last strings to join.
	 * @param strings the strings to join.
	 * @return a new string will all the provided {@code strings} joined using the separators.
	 */
	public static String joinGrammatically(CharSequence regularSeparator, CharSequence finalSeparator, List<String> strings) {
	    int size = strings == null ? 0 : strings.size();
		return switch (size) {
			case 0 -> "";
			case 1 -> strings.get(0);
			default -> String.join(regularSeparator, strings.subList(0, --size)) + finalSeparator + strings.get(size);
		};
	}


	/**
	 * Create a {@link String} that repeats the base character n times.
	 * @param base the base character.
	 * @param n the number of repetition.
	 * @return a {@link String} that repeats the base character n times.
	 */
	public static String repeat(char base, int n) {
		char[] chars = new char[n];
		Arrays.fill(chars, base);
		return String.valueOf(chars);
	}





	private static final Pattern endingNumber = Pattern.compile("(\\d+)$");


	/**
	 * Generate a name based on the original name, but that does not conflit with anouther one, according to the
	 * provided predicate.
	 * It can be used to to add an entry in a map when the key already exists, and it is ok to modify the added key to
	 * not erase the previous data.
	 * This situation can be compared to when a file is added to a directory but another file with the same name exists,
	 * so the new file have a suffix number to make the file name different.
	 * <p>
	 * Be aware that this method may run an infinite loop if the predicate continuously returns true.
	 * @param originalName the original conflicting name.
	 * @param conflictTester a predicate that test if a generated name stills conflict with a set of name
	 * @return the original name, with a suffix number (ex: {@code "original1"}).
	 */
	public static String fixConflictName(String originalName, Predicate<String> conflictTester) {
		int suffix = 1;
		Matcher endingMatcher = endingNumber.matcher(originalName);
		if (endingMatcher.find()) {
			suffix = Integer.parseInt(endingMatcher.group(1)) + 1;
			originalName = originalName.substring(0, endingMatcher.start());
		}

		for (;; suffix++) {
			String newStr = originalName + suffix;
			if (!conflictTester.test(newStr))
				return newStr;
		}
	}
}
