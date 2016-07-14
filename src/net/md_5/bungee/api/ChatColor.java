/*
 * Decompiled with CFR 0_114.
 */
package net.md_5.bungee.api;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public enum ChatColor {
	BLACK('0', "black"),
	DARK_BLUE('1', "dark_blue"),
	DARK_GREEN('2', "dark_green"),
	DARK_AQUA('3', "dark_aqua"),
	DARK_RED('4', "dark_red"),
	DARK_PURPLE('5', "dark_purple"),
	GOLD('6', "gold"),
	GRAY('7', "gray"),
	DARK_GRAY('8', "dark_gray"),
	BLUE('9', "blue"),
	GREEN('a', "green"),
	AQUA('b', "aqua"),
	RED('c', "red"),
	LIGHT_PURPLE('d', "light_purple"),
	YELLOW('e', "yellow"),
	WHITE('f', "white"),
	MAGIC('k', "obfuscated"),
	BOLD('l', "bold"),
	STRIKETHROUGH('m', "strikethrough"),
	UNDERLINE('n', "underline"),
	ITALIC('o', "italic"),
	RESET('r', "reset");

	public static final char COLOR_CHAR = '\u00a7';
	public static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";
	public static final Pattern STRIP_COLOR_PATTERN;
	private static final Map<Character, ChatColor> BY_CHAR;
	private final char code;
	private final String toString;
	private final String name;

	private ChatColor(char code, String name) {
		this.code = code;
		this.name = name;
		toString = new String(new char[] { '\u00a7', code });
	}

	@Override
	public String toString() {
		return toString;
	}

	public static String stripColor(String input) {
		if (input == null) return null;
		return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
	}

	public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
		char[] b2 = textToTranslate.toCharArray();
		for (int i = 0; i < b2.length - 1; ++i) {
			if (b2[i] != altColorChar || "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b2[i + 1]) <= -1) continue;
			b2[i] = 167;
			b2[i + 1] = Character.toLowerCase(b2[i + 1]);
		}
		return new String(b2);
	}

	public static ChatColor getByChar(char code) {
		return BY_CHAR.get(Character.valueOf(code));
	}

	public String getName() {
		return name;
	}

	static {
		STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf('\u00a7') + "[0-9A-FK-OR]");
		BY_CHAR = new HashMap<Character, ChatColor>();
		for (ChatColor colour : ChatColor.values())
			BY_CHAR.put(Character.valueOf(colour.code), colour);
	}
}
