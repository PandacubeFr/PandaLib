package fr.pandacube.lib.permissions;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import fr.pandacube.lib.permissions.PermissionsCachedBackendReader.CachedPlayer;
import fr.pandacube.lib.permissions.SQLPermissions.EntityType;

public class PermPlayer extends PermEntity {
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
	 * Alias for {@link #getInheritances()}.
	 */
	public List<PermGroup> getGroups() {
		return getInheritances();
	}
	
	/**
	 * Alias for {@link #getInheritances()}.
	 */
	public List<String> getGroupsString() {
		return getInheritancesString();
	}
	
	/**
	 * Tells if the player is directly part of a group.
	 * This is equivalent to {@link #inheritsFromGroup(String, boolean) inheritsFromGroup(group, false)}
	 * @param group the group to search for
	 */
	public boolean isInGroup(String group) {
		return inheritsFromGroup(group, false);
	}
	
	public boolean isUsingDefaultGroups() {
		return getBackendEntity().usingDefaultGroups;
	}
	
	public void setGroup(String group) {
		Permissions.backendWriter.setInheritance(name, type, group);
	}
	
	public void setGroup(PermGroup group) {
		setGroup(group.name);
	}
	
	public void addGroup(String group) {
		Permissions.backendWriter.addInheritance(name, type, group);
	}
	
	public void addGroup(PermGroup group) {
		addGroup(group.name);
	}
	
	public void removeGroup(String group) {
		Permissions.backendWriter.removeInheritance(name, type, group);
	}
	
	public void removeGroup(PermGroup group) {
		removeGroup(group.name);
	}
}