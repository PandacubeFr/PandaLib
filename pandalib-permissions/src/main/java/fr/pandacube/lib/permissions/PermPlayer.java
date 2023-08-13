package fr.pandacube.lib.permissions;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import fr.pandacube.lib.permissions.PermissionsCachedBackendReader.CachedPlayer;
import fr.pandacube.lib.permissions.SQLPermissions.EntityType;

/**
 * Represents a player in the permission system.
 */
public sealed class PermPlayer extends PermEntity permits DefaultPlayer {
	private final UUID playerId;
	/* package */ PermPlayer(UUID id) {
		super(id.toString(), EntityType.User);
		playerId = id;
	}
	@Override
	protected CachedPlayer getBackendEntity() {
		return Permissions.backendReader.getCachedPlayer(playerId);
	}
	
	@Override
	public List<PermGroup> getInheritances() {
		return PermGroup.fromCachedGroups(getBackendEntity().groups);
	}
	
	@Override
	public List<String> getInheritancesString() {
		return getBackendEntity().groups.stream()
				.map(cg -> cg.name)
				.collect(Collectors.toList());
	}

	/**
	 * Gets the UUID of this player.
	 * @return the UUID of this player.
	 */
	public UUID getPlayerId() {
		return playerId;
	}
	
	private String cachedPlayerName;
	@Override
	public synchronized String getName() {
		if (cachedPlayerName == null)
			cachePlayerName();
		return cachedPlayerName;
	}
	private void cachePlayerName() {
		cachedPlayerName = Permissions.playerNameGetter.apply(playerId);
		if (cachedPlayerName == null)
			cachedPlayerName = playerId.toString();
	}
	
	/**
	 * Gets all the groups this player belongs to.
	 * Alias for {@link #getInheritances()}.
	 * @return a list of all the groups this player belongs to.
	 */
	public List<PermGroup> getGroups() {
		return getInheritances();
	}
	
	/**
	 * Gets all the group names this player belongs to.
	 * Alias for {@link #getInheritances()}.
	 * @return a list of all the group names this player belongs to.
	 */
	public List<String> getGroupsString() {
		return getInheritancesString();
	}
	
	/**
	 * Tells if the player is directly part of a group.
	 * This is equivalent to {@link #inheritsFromGroup(String, boolean) inheritsFromGroup(group, false)}
	 * @param group the group to search for
	 * @return true if the player is directly part of a group, false otherwise.
	 */
	public boolean isInGroup(String group) {
		return inheritsFromGroup(group, false);
	}

	/**
	 * Tells if this player has been assigned to the default groups.
	 * @return true if this player has been assigned to the default groups, or false if this player belongs explicitly
	 *         to their groups.
	 */
	public boolean isUsingDefaultGroups() {
		return getBackendEntity().usingDefaultGroups;
	}

	/**
	 * Sets the group this player will now inherit, removing all previously inherited groups.
	 * To keep the other inherited groups, use {@link #addGroup(String)}.
	 * @param group the name of the group to inherit from.
	 */
	public void setGroup(String group) {
		Permissions.backendWriter.setInheritance(name, type, group);
	}

	/**
	 * Sets the group this player will now inherit, removing all previously inherited groups.
	 * To keep the other inherited groups, use {@link #addGroup(PermGroup)}.
	 * @param group the group to inherit from.
	 */
	public void setGroup(PermGroup group) {
		setGroup(group.name);
	}

	/**
	 * Makes this player inherit the provided group, keeping the other groups they already inherit from.
	 * @param group the name of the group to inherit from.
	 */
	public void addGroup(String group) {
		Permissions.backendWriter.addInheritance(name, type, group);
	}

	/**
	 * Makes this player inherit the provided group, keeping the other groups they already inherit from.
	 * @param group the group to inherit from.
	 */
	public void addGroup(PermGroup group) {
		addGroup(group.name);
	}

	/**
	 * Makes this player stop inheriting from the provided group.
	 * @param group the name of the group to stop inheriting from.
	 */
	public void removeGroup(String group) {
		Permissions.backendWriter.removeInheritance(name, type, group);
	}

	/**
	 * Makes this player stop inheriting from the provided group.
	 * @param group the group to stop inheriting from.
	 */
	public void removeGroup(PermGroup group) {
		removeGroup(group.name);
	}
}