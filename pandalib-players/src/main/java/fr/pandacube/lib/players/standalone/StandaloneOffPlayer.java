package fr.pandacube.lib.players.standalone;

import java.util.UUID;

public interface StandaloneOffPlayer {

	
	
	
	/*
	 * General data and state
	 */
	
	/**
	 * Return the ID of the minecraft account.
	 * 
	 * @return the id of the player
	 */
	UUID getUniqueId();

	/**
	 * @return the last known player name of this player, or null if this player never joined the network.
	 */
	String getName();
	
	/**
	 * Indicate if this player is connected to the current node (server or proxy, depending on interface implementation)
	 * @return wether the player is online or not
	 */
	boolean isOnline();
	
	

	

	
	
	/*
	 * Related class instances
	 */
	
	/**
	 * Return the online instance of this player, if any exists.
	 * May return itself if the current instance already represent an online player.
	 */
	StandaloneOnlinePlayer getOnlineInstance();

	
	
	
	
	
	/*
	 * Display name
	 */
	
	/**
	 * Returns the name of the player (if any), with eventual prefix and suffix depending on permission groups
	 * (and team for bukkit implementation)
	 * @return the display name of the player
	 */
	String getDisplayName();

	

	

	

}
