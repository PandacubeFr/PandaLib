package fr.pandacube.lib.util;

import java.time.Duration;

/**
 * Provides methods related to Minecraft Java server ticks.
 */
public class Tick {

	/**
	 * The number of Minecraft server ticks in a second.
	 */
	public static final int TPS = 20;

	/**
	 * The duration of a Minecraft server tick in millisecond.
	 */
	public static final int MS_PER_TICK = 1000 / TPS;
	
	
	/**
	 * Returns the number of ticks for the provided number of seconds.
	 * @param seconds the duration in seconds
	 * @return the same duration as provided, but in Minecraft server ticks
	 */
	public static long ofSec(long seconds) {
		return seconds * TPS;
	}
	
	/**
	 * Returns the number of ticks for the provided number of minutes.
	 * @param minutes the duration in minutes
	 * @return the same duration as provided, but in Minecraft server ticks
	 */
	public static long ofMin(long minutes) {
		return minutes * TPS * 60;
	}


	/**
	 * Returns the number of milliseconds for the provided number of ticks.
	 * @param tick the duration in ticks
	 * @return the same duration as provided, but in milliseconds
	 */
	public static long toMs(long tick) {
		return tick * MS_PER_TICK;
	}


	/**
	 * Returns the {@link Duration} for the provided number of ticks.
	 * @param tick the duration in ticks
	 * @return the {@link Duration} for the provided number of ticks.
	 */
	public static Duration toDuration(long tick) {
		return Duration.ofMillis(toMs(tick));
	}

	private Tick() {}

}
