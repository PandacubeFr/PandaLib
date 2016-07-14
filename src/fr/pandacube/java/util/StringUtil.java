package fr.pandacube.java.util;

public class StringUtil {
	public static String formatDouble(double d) {
		if (d == (long) d) return String.format("%d", (long) d);
		else
			return String.valueOf(d);
	}

	/**
	 * @param s Chaine de caractère à parcourir
	 * @param c_match le caractère dont on doit retourner le nombre d'occurence
	 * @return nombre d'occurence de <b>c_match</b> dans <b>s</b>
	 */
	public static int char_count(CharSequence s, char c_match) {
		char[] chars = s.toString().toCharArray();
		int count = 0;
		for (char c : chars)
			if (c == c_match) count++;
		return count;
	}
}
