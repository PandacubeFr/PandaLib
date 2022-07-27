package fr.pandacube.lib.util;

/**
 * Provides methods related to Minecraft Java server ticks.
 */
public class Tick {
	
	
	/**
	 * Returns the number of tick is the provided number of seconds.
	 * @param seconds the duration in second
	 * @return the same duration as provided, but in Minecraft server ticks
	 */
	public static long sec(long seconds) {
		return seconds * 20;
	}
	
	/**
	 * Returns the number of tick is the provided number of minutes.
	 * @param minutes the duration in minutes
	 * @return the same duration as provided, but in Minecraft server ticks
	 */
	public static long min(long minutes) {
		return minutes * 1200;
	}
	
	
	
	
	
	

}
