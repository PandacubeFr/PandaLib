package fr.pandacube.lib.players.standalone;

import java.util.UUID;
import java.util.function.UnaryOperator;

/**
 * Represents any player, either offline or online.
 */
public interface AbstractOffPlayer {

	
	
	
	/*
	 * General data and state
	 */
	
	/**
	 * Returns the {@link UUID} of this player.
	 * @return the id of the player
	 */
	UUID getUniqueId();

	/**
	 * The last known player name of this player, or null if it is not known.
	 * @return the last known player name of this player, or null if it is not known.
	 */
	String getName();
	
	/**
	 * Indicate if this player is connected to the current node (server or proxy, depending on interface implementation).
	 * @return weather the player is online or not.
	 */
	boolean isOnline();
	
	

	

	
	
	/*
	 * Related class instances
	 */
	
	/**
	 * Return the online instance of this player, if any exists.
	 * May return itself if the current instance already represent an online player.
	 * @return the online instance for this player.
	 */
	AbstractOnlinePlayer getOnlineInstance();

	
	
	
	
	
	/*
	 * Display name
	 */
	
	/**
	 * Returns the display name of the player (if any), with eventual prefix and suffix depending on permission groups
	 * (and team for bukkit implementation).
	 * @return the display name of the player.
	 */
	String getDisplayName();





	/*
	 * Player config
	 */

	/**
	 * Gets the value of the provided configuration key of this player.
	 * @param key the configuration key.
	 * @return the value of the configuration, or null if the configuration is not set.
	 * @throws Exception if an error occurs fetching the configuration value.
	 */
	String getConfig(String key) throws Exception;

	/**
	 * Gets the value of the provided configuration key of this player.
	 * @param key the configuration key.
	 * @param deflt the default value if the configuration is not set.
	 * @return the value of the configuration, or {@code deflt} if the configuration is not set.
	 * @throws Exception if an error occurs fetching the configuration value.
	 */
	String getConfig(String key, String deflt) throws Exception;

	/**
	 * Sets the value of the provided configuration key for this player.
	 * @param key the configuration key to set.
	 * @param value the new value.
	 * @throws Exception if an error occurs updating the configuration value.
	 */
	void setConfig(String key, String value) throws Exception;

	/**
	 * Updates the value of the provided configuration key for this player, using the provided updater.
	 * @param key the configuration key to update.
	 * @param deflt the default value to use if the configuration is not already set.
	 * @param updater the unary operator to use to update th value. The old value is used as the parameter of the updater,
	 *                and it returns the new value of the configuration.
	 * @throws Exception if an error occurs updating the configuration value.
	 */
	void updateConfig(String key, String deflt, UnaryOperator<String> updater) throws Exception;

	/**
	 * Unsets the value of the provided configuration key for this player.
	 * @param key the configuration key to update.
	 * @throws Exception if an error occurs deleting the configuration value.
	 */
	void unsetConfig(String key) throws Exception;

	

	

	

}
