package fr.pandacube.lib.core.util;

public class Tick {
	
	
	/**
	 * Returns the number of tick is the provided duration, in second
	 * @param seconds the duration in second
	 * @return the same duration as provided, but in Minecraft server ticks
	 */
	public static long sec(long seconds) {
		return seconds * 20;
	}
	
	/**
	 * Returns the number of tick is the provided duration, in second
	 * @param minutes the duration in minutes
	 * @return the same duration as provided, but in Minecraft server ticks
	 */
	public static long min(long minutes) {
		return minutes * 1200;
	}
	
	
	
	
	
	

}
