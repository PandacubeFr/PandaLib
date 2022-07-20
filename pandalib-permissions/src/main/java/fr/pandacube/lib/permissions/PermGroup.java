package fr.pandacube.lib.permissions;

import java.util.List;
import java.util.stream.Collectors;

import fr.pandacube.lib.permissions.PermissionsCachedBackendReader.CachedGroup;
import fr.pandacube.lib.permissions.SQLPermissions.EntityType;

public class PermGroup extends PermEntity {
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
	
	public boolean isDefault() {
		return getBackendEntity().deflt;
	}
	
	public void setDefault(boolean deflt) {
		Permissions.backendWriter.setGroupDefault(name, deflt);
	}
	
	public void addInheritance(String group) {
		Permissions.backendWriter.addInheritance(name, type, group);
	}
	
	public void addInheritance(PermGroup group) {
		addInheritance(group.name);
	}
	
	public void removeInheritance(String group) {
		Permissions.backendWriter.removeInheritance(name, type, group);
	}
	
	public void removeInheritance(PermGroup group) {
		removeInheritance(group.name);
	}
	
	/* package */ static List<PermGroup> fromCachedGroups(List<CachedGroup> in) {
		return in.stream()
				.map(cg -> Permissions.getGroup(cg.name))
				.collect(Collectors.toList());
	}
}