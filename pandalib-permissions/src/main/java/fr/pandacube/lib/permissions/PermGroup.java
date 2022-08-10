package fr.pandacube.lib.permissions;

import java.util.List;
import java.util.stream.Collectors;

import fr.pandacube.lib.permissions.PermissionsCachedBackendReader.CachedGroup;
import fr.pandacube.lib.permissions.SQLPermissions.EntityType;

/**
 * Represents an group in the permission system.
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
	 * Tells if this group is a default group.
	 * A player inherits all default groups when they don’t explicitely inherit from at least one group.
	 * @return true if this group is a default group, false otherwise.
	 */
	public boolean isDefault() {
		return getBackendEntity().deflt;
	}

	/**
	 * Sets this group as a default group or not.
	 * All players that don’t explicitely inherit from at least one group will either start or stop implicitely
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