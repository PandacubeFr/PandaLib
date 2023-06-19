package fr.pandacube.lib.permissions;

import fr.pandacube.lib.db.DBException;
import fr.pandacube.lib.permissions.PermissionsCachedBackendReader.CachedGroup;
import fr.pandacube.lib.permissions.SQLPermissions.EntityType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Represents a group in the permission system.
 */
public final class PermGroup extends PermEntity {
	/* package */ PermGroup(String name) {
		super(name, EntityType.Group);
	}
	@Override
	protected CachedGroup getBackendEntity() {
		return Permissions.backendReader.getCachedGroup(name);
	}
	
	@Override
	public String getName() {
		return getInternalName();
	}
	
	@Override
	public List<PermGroup> getInheritances() {
		return fromCachedGroups(getBackendEntity().inheritances);
	}
	
	@Override
	public List<String> getInheritancesString() {
		return getBackendEntity().inheritances.stream()
				.map(cg -> cg.name)
				.collect(Collectors.toList());
	}

	/**
	 * Gets all the groups that directly inherits from this group.
	 * @return the groups that directly inherits from this group.
	 */
	public List<PermGroup> getInheritedGroups() {
		CachedGroup thisCG = getBackendEntity();
		return fromCachedGroups(Permissions.backendReader.getGroups().stream()
				.filter(cg -> cg.inheritances.contains(thisCG))
				.toList());
	}

	/**
	 * Gets all the players that inherits from this group.
	 * This method does not use cached data.
	 * @param recursive true to include players that are in inherited groups.
	 * @return the players that inherit from this group.
	 * @throws DBException if a database error occurs.
	 */
	public Set<UUID> getInheritedPlayers(boolean recursive) throws DBException {
		Set<UUID> players = new HashSet<>(getBackendEntity().getPlayersInGroup());
		if (recursive) {
			for (PermGroup inheritedGroups : getInheritedGroups()) {
				players.addAll(inheritedGroups.getInheritedPlayers(true));
			}
		}
		return players;
	}

	/**
	 * Tells if this group is a default group.
	 * A player inherits all default groups when they don’t explicitly inherit from at least one group.
	 * @return true if this group is a default group, false otherwise.
	 */
	public boolean isDefault() {
		return getBackendEntity().deflt;
	}

	/**
	 * Sets this group as a default group or not.
	 * All players that don’t explicitly inherit from at least one group will either start or stop implicitly
	 * inheriting from this group.
	 * @param deflt true to set this group as default, false to set is as not default.
	 */
	public void setDefault(boolean deflt) {
		Permissions.backendWriter.setGroupDefault(name, deflt);
	}

	/**
	 * Makes this group inherit the provided group.
	 * @param group the name of the group to inherit from.
	 */
	public void addInheritance(String group) {
		Permissions.backendWriter.addInheritance(name, type, group);
	}

	/**
	 * Makes this group inherit the provided group.
	 * @param group the group to inherit from.
	 */
	public void addInheritance(PermGroup group) {
		addInheritance(group.name);
	}

	/**
	 * Makes this group stop inheriting from the provided group.
	 * @param group the name of the group to stop inheriting from.
	 */
	public void removeInheritance(String group) {
		Permissions.backendWriter.removeInheritance(name, type, group);
	}

	/**
	 * Makes this group stop inheriting from the provided group.
	 * @param group the group to stop inheriting from.
	 */
	public void removeInheritance(PermGroup group) {
		removeInheritance(group.name);
	}
	
	/* package */ static List<PermGroup> fromCachedGroups(List<CachedGroup> in) {
		return in.stream()
				.map(cg -> Permissions.getGroup(cg.name))
				.collect(Collectors.toList());
	}
}