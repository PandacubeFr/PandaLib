package fr.pandacube.lib.players.permissible;

import java.util.OptionalLong;
import java.util.stream.LongStream;

import fr.pandacube.lib.players.standalone.StandaloneOnlinePlayer;

public interface PermissibleOnlinePlayer extends PermissibleOffPlayer, StandaloneOnlinePlayer {




	/*
	 * General data and state
	 */

	/**
	 * @return The current name of this player
	 * @implSpec The implementation is expected to call the environment API
	 * (Bukkit/Bungee) to get the name of the player.
	 */
	String getName();
	
	
	
	
	
	
	
	/*
	 * Permissions and groups
	 */

	/**
	 * Tells if this online player has the specified permission.
	 * @implSpec the implementation of this method must not directly or
	 * indirectly call the method {@link PermissibleOffPlayer#hasPermission(String)},
	 * or it may result in a {@link StackOverflowError}.
	 */
	boolean hasPermission(String permission);

	/**
	 * Tells if this online player has the permission resulted from the provided expression.
	 * @implSpec the implementation of this method must not directly or
	 * indirectly call the method {@link PermissibleOffPlayer#hasPermissionExpression(String)},
	 * or it may result in a {@link StackOverflowError}.
	 */
	boolean hasPermissionExpression(String permission);

	/**
	 * Lists all the values for a set of permission indicating an integer in a range.
	 * <p>
	 * A permission range is used to easily attribute a number to a group or player,
	 * like the maximum number of homes allowed. For instance, if the player has the permission
	 * {@code essentials.home.12}, this method would return a stream containing the value 12,
	 * if the parameter {@code permissionPrefix} is {@code "essentials.home."}.
	 * <p>
	 * The use of a stream allow the caller to get either the maximum, the minimum, or do any
	 * other treatment to the values.
	 * @param permissionPrefix the permission prefix to search for.
	 * @return a LongStream containing all the values found for the specified permission prefix.
	 */
	LongStream getPermissionRangeValues(String permissionPrefix);
	
	/**
	 * Returns the maximum value returned by {@link PermissibleOffPlayer#getPermissionRangeValues(String)}.
	 */
	OptionalLong getPermissionRangeMax(String permissionPrefix);
	


}
