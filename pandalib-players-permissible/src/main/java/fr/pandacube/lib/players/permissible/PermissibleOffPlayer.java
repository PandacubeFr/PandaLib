package fr.pandacube.lib.players.permissible;

import java.util.OptionalLong;
import java.util.stream.LongStream;

import fr.pandacube.lib.chat.ChatColorUtil;
import fr.pandacube.lib.permissions.PermPlayer;
import fr.pandacube.lib.permissions.Permissions;
import fr.pandacube.lib.players.standalone.StandaloneOffPlayer;

public interface PermissibleOffPlayer extends StandaloneOffPlayer {

	
	
	
	/*
	 * Related class instances
	 */
	
	/**
	 * Return the online instance of this player, if any exists.
	 * May return itself if the current instance already represent an online player.
	 */
	PermissibleOnlinePlayer getOnlineInstance();

	/**
	 * Get the permission instance of this player. This will never return null.
	 * @return the permission instance of this player 
	 */
	default PermPlayer getPermissionUser() {
		return Permissions.getPlayer(getUniqueId());
	}
	
	
	
	
	
	
	/*
	 * Display name
	 */

	/**
	 * Get an updated display name of the user,
	 * generated using eventual permission’s prefix(es) and suffix(es) of the player,
	 * and with color codes translated to Minecraft’s native {@code §}.
	 */
	default String getDisplayNameFromPermissionSystem() {
		PermPlayer permU = getPermissionUser();
		return ChatColorUtil.translateAlternateColorCodes('&',
				permU.getPrefix() + getName() + permU.getSuffix());
	}
	
	
	
	
	
	
	/*
	 * Permissions and groups
	 */
	
	/**
	 * Tells if this player has the specified permission.
	 * If the player is online, this will redirect the
	 * method call to the {@link PermissibleOnlinePlayer} instance,
	 * that MUST override this current method to avoid recussive
	 * loop.
	 * If the player is offline, it just call the Pandacube
	 * permission system.
	 * @param permission the permission node to test
	 * @return whether this player has the provided permission
	 */
	default boolean hasPermission(String permission) {
		PermissibleOnlinePlayer online = getOnlineInstance();
		
		if (online != null)
			return online.hasPermission(permission);
		
		// at this point, the player is offline
		return getPermissionUser().hasPermissionOr(permission, null, null, false);
	}
	
	/**
	 * Tells if this player has the permission resulted from the provided expression.
	 * If the player is online, this will redirect the
	 * method call to the {@link PermissibleOnlinePlayer} instance,
	 * that MUST override this current method to avoid recussive
	 * loop.
	 * If the player is offline, it just call the Pandacube
	 * permission system.
	 * @param permissionExpression the permission node to test
	 * @return whether this player has the provided permission
	 */
	default boolean hasPermissionExpression(String permissionExpression) {
		PermissibleOnlinePlayer online = getOnlineInstance();
		
		if (online != null)
			return online.hasPermissionExpression(permissionExpression);
		
		// at this point, the player is offline
		return getPermissionUser().hasPermissionExpression(permissionExpression, null, null);
	}

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
	default LongStream getPermissionRangeValues(String permissionPrefix) {
		PermissibleOnlinePlayer online = getOnlineInstance();
		
		if (online != null)
			return online.getPermissionRangeValues(permissionPrefix);
		
		// at this point, the player is offline
		return getPermissionUser().getPermissionRangeValues(permissionPrefix, null, null);
	}
	
	/**
	 * Returns the maximum value returned by {@link PermissibleOffPlayer#getPermissionRangeValues(String)}.
	 */
	default OptionalLong getPermissionRangeMax(String permissionPrefix) {
		PermissibleOnlinePlayer online = getOnlineInstance();
		
		if (online != null)
			return online.getPermissionRangeMax(permissionPrefix);
		
		// at this point, the player is offline
		return getPermissionUser().getPermissionRangeMax(permissionPrefix, null, null);
	}

	/**
	 * Tells if the this player is part of the specified group
	 * 
	 * @param group the permissions group
	 * @return <i>true</i> if this player is part of the group,
	 *         <i>false</i> otherwise
	 */
	default boolean isInGroup(String group) {
		return getPermissionUser().isInGroup(group);
	}
	

}
