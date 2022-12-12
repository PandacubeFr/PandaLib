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
	 * @return wether the player is online or not.
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

	String getConfig(String key) throws Exception;

	String getConfig(String key, String deflt) throws Exception;

	void setConfig(String key, String value) throws Exception;

	void updateConfig(String key, String deflt, UnaryOperator<String> updater) throws Exception;

	void unsetConfig(String key) throws Exception;

	

	

	

}
