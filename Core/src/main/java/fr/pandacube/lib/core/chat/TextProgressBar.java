package fr.pandacube.lib.core.chat;

import net.md_5.bungee.api.ChatColor;

public class TextProgressBar {
	private static String pattern_start = "[";
	private static String pattern_end = "]";
	private static ChatColor color_empty = ChatColor.DARK_GRAY;
	private static ChatColor color_default = ChatColor.RESET;
	private static String pattern_empty = ".";
	private static String pattern_full = "|";

	public static String progressBar(double[] values, ChatColor[] colors, double total, int nbCar) {
		long[] sizes = new long[values.length];

		int max_size = nbCar - pattern_start.length() - pattern_end.length();

		for (int i = 0; i < values.length; i++) {
			double sum_values_before = 0;
			for (int j = i; j >= 0; j--)
				sum_values_before += values[j];

			long car_position = Math.round(max_size * sum_values_before / total);

			// évite les barre de progressions plus grandes que la taille
			// demandée
			if (car_position > max_size) car_position = max_size;

			long sum_sizes_before = 0;
			for (int j = i - 1; j >= 0; j--)
				sum_sizes_before += sizes[j];

			sizes[i] = car_position - sum_sizes_before;
		}
		int sum_sizes = 0;

		String bar = pattern_start;
		for (int i = 0; i < sizes.length; i++) {
			sum_sizes += sizes[i];

			ChatColor color = color_default;
			if (colors != null && i < colors.length && colors[i] != null) color = colors[i];

			bar = bar + color;

			for (int j = 0; j < sizes[i]; j++)
				bar = bar + pattern_full;
		}

		bar = bar + color_empty;
		for (int j = 0; j < (max_size - sum_sizes); j++)
			bar = bar + pattern_empty;

		bar = bar + ChatColor.RESET + pattern_end;
		return bar;
	}

	public static String progressBar(double value, ChatColor color, double max, int nbCar) {
		return progressBar(new double[] { value }, new ChatColor[] { color }, max, nbCar);
	}

}
