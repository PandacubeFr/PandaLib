package fr.pandacube.lib.core.util;

import java.util.List;

public class StringUtil {
	public static String formatDouble(double d) {
		if (d == (long) d)
			return String.format("%d", (long) d);
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
	
	
	
	
	public static String joinGrammatically(CharSequence sep1, CharSequence sepFinal, List<String> strings) {
	    int size = strings == null ? 0 : strings.size();
	    return size == 0 ? "" : size == 1 ? strings.get(0) : String.join(sep1, strings.subList(0, --size)) + sepFinal + strings.get(size);
	}
	
	
	
	

	public static String repeat(String base, int count) {
		int baseLength = base.length();
		char[] baseChars = base.toCharArray();
		char[] chars = new char[baseLength * count];
		for (int i = 0; i < count; i++) {
			System.arraycopy(baseChars, 0, chars, i * baseLength, baseLength);
		}
		return new String(chars);
	}
	
	
	public static String repeat(char base, int count) {
		char[] chars = new char[count];
		for (int i = 0; i < count; i++) {
			chars[i] = base;
		}
		return new String(chars);
	}
}