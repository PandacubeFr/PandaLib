package fr.pandacube.lib.core.players;

import java.util.UUID;

import fr.pandacube.lib.core.chat.ChatColorUtil;
import fr.pandacube.lib.core.db.DBException;
import fr.pandacube.lib.core.permissions.PermPlayer;
import fr.pandacube.lib.core.permissions.Permissions;
import fr.pandacube.lib.core.util.Log;

public interface IOffPlayer {
	
	
	
	
	/*
	 * General data and state
	 */
	
	/**
	 * @return the id of the player
	 */
	public abstract UUID getUniqueId();

	/**
	 * @return the last known player name of this player, or null if this player never joined the network.
	 */
	public default String getName() {
		return PlayerFinder.getLastKnownName(getUniqueId());
	}
	
	/**
	 * Indicate if this player is connected to the current node (server or proxy, depending on interface implementation)
	 * @return wether the player is online or not
	 */
	public abstract boolean isOnline();
	
	
	
	
	/*
	 * Related class instances
	 */
	
	/**
	 * Return the online instance of this player, if any exists.
	 * May return itself if the current instance already represent an online player.
	 */
	public abstract IOnlinePlayer getOnlineInstance();
	
	/**
	 * Get the database entry of this player, or null if the player never joined the network.
	 * @throws DBException
	 */
	public default SQLPlayer getDbPlayer() throws DBException {
		return SQLPlayer.getPlayerFromUUID(getUniqueId());
	}

	/**
	 * Get the permission instance of this player. This will never return null.
	 * @return the permission instance of this player 
	 */
	public default PermPlayer getPermissionUser() {
		return Permissions.getPlayer(getUniqueId());
	}
	
	
	
	
	
	
	/*
	 * Display name
	 */
	
	/**
	 * Returns the name of the player (if any), with eventual prefix and suffix depending on permission groups
	 * (and team for bukkit implementation)
	 * @return the display name of the player
	 */
	public abstract String getDisplayName();

	/**
	 * Get an updated display name of the user,
	 * generated using eventual permission’s prefix(es) and suffix(es) of the player,
	 * and with color codes translated to Minecraft’s native {@code §}.
	 * @return 
	 */
	public default String getDisplayNameFromPermissionSystem() {
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
	 * method call to the {@link IOnlinePlayer} instance,
	 * that MUST override this current method to avoid recussive
	 * loop.
	 * If the player is offline, it just call the Pandacube
	 * permission system.
	 * @param permission the permission node to test
	 * @return whether this player has the provided permission
	 */
	public default boolean hasPermission(String permission) {
		IOnlinePlayer online = getOnlineInstance();
		
		if (online != null)
			return online.hasPermission(permission);
		
		// at this point, the player is offline
		Boolean res = getPermissionUser().hasPermission(permission);
		return res != null ? res : false;
	}

	/**
	 * Tells if the this player is part of the specified group
	 * 
	 * @param group the permissions group
	 * @return <i>true</i> if this player is part of the group,
	 *         <i>false</i> otherwise
	 */
	public default boolean isInGroup(String group) {
		return getPermissionUser().isInGroup(group);
	}

	/**
	 * Tells if this player is part of the staff, based on permission groups
	 */
	public default boolean isInStaff() {
		return getPermissionUser().inheritsFromGroup("staff-base", true);
	}
	
	
	
	
	
	
	
	/*
	 * Ignore
	 */
	
	/**
	 * Tells if this player have the right to ignore the provided player
	 * @param ignored the player that is potentially ignored by this player.
	 *                If this parameter is null, this method returns false.
	 */
	public default boolean canIgnore(IOffPlayer ignored) {
		if (ignored == null)
			return false;
		if (equals(ignored))
			return false;
		if (!isInStaff() && !ignored.isInStaff())
			return true;
		return hasPermission("pandacube.ignore.bypassfor." + ignored.getUniqueId());
	}
	
	/**
	 * Tells if the provided player have the right to ignore this player
	 * @param ignorer the player that potentially ignore this player
	 *                If this parameter is null, this method returns false.
	 * @implNote the default implementation just calls {@link #canIgnore(IOffPlayer) ignorer.canIgnore(this)}.
	 */
	public default boolean canBeIgnoredBy(IOffPlayer ignorer) {
		if (ignorer == null)
			return false;
		return ignorer.canIgnore(this);
	}

	/**
	 * Determine if this player ignore the provided player.
	 * @param ignored the player that is potentially ignored by this player.
	 *                If this parameter is null, this method returns false.
	 * @return true if this player have to right to ignore the provided player and is actually ignoring him.
	 */
	public default boolean isIgnoring(IOffPlayer ignored) {
		if (!canIgnore(ignored))
			return false;

		try {
			return SQLPlayerIgnore.isPlayerIgnoringPlayer(getUniqueId(), ignored.getUniqueId());
		} catch (DBException e) {
			Log.severe("Can't determine if a player ignore another player, because we can't access to the database", e);
			return false;
		}
	}
	

	/**
	 * Determine if the provided player ignore this player, taking into account the exception permissions.
	 * @param ignorer the player that potentially ignore this player
	 *                If this parameter is null, this method returns false.
	 * @return true if the provided player have to right to ignore this player and is actually ignoring him.
	 * @implNote the default implementation just calls {@link #isIgnoring(IOffPlayer) ignorer.isIgnoring(this)}.
	 */
	public default boolean isIgnoredBy(IOffPlayer ignorer) {
		return ignorer.isIgnoring(this);
	}
	
	
	
	
	
	/*
	 * Modération
	 */

	/**
	 * Retrieve the time when the player will be unmuted, or null if the player is not muted.
	 * @return the timestamp in millisecond of when the player will be unmuted
	 */
	public default Long getMuteTimeout() {
		try {
			Long muteTimeout = getDbPlayer().get(SQLPlayer.muteTimeout);
			if (muteTimeout == null || muteTimeout <= System.currentTimeMillis())
				return null;
			return muteTimeout;
		} catch (DBException e) {
			Log.severe(e);
			return null;
		}
	}

	/**
	 * Tells if the player is currently muted, meaning that they cannot communicate
	 * through the chat or private messages.
	 * @return
	 */
	public default boolean isMuted() {
		return getMuteTimeout() != null;
	}
	
	
	

}