package fr.pandacube.lib.core.permissions;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import fr.pandacube.lib.core.chat.ChatUtil.DisplayTreeNode;
import fr.pandacube.lib.core.permissions.PermissionsCachedBackendReader.CachedEntity;
import fr.pandacube.lib.core.permissions.SQLPermissions.EntityType;

public abstract class PermEntity {
	protected final String name;
	protected final EntityType type;
	protected PermEntity(String n, EntityType t) {
		name = n; type = t;
	}
	
	protected abstract CachedEntity getBackendEntity();
	public abstract List<PermGroup> getInheritances();
	public abstract List<String> getInheritancesString();
	public abstract String getName();
	
	public String getInternalName() {
		return name;
	}
	
	
	/**
	 * Tells if the current entity inherits directly or indirectly from the specified group
	 * @param group the group to search for
	 * @param recursive true to search in the inheritance tree, or false to search only in the inheritance list of the current entity.
	 * @return
	 */
	public boolean inheritsFromGroup(String group, boolean recursive) {
		if (group == null)
			return false;
		return getInheritances().stream().anyMatch(g -> g.name.equals(group) || (recursive && g.inheritsFromGroup(group, recursive)));
	}
	
	public String getPrefix() {
		return Permissions.resolver.getEffectivePrefix(name, type);
	}
	
	
	public String getSelfPrefix() {
		return getBackendEntity().getSelfPrefix();
	}
	
	
	public DisplayTreeNode debugPrefix() {
		return Permissions.resolver.debugPrefix(name, type);
	}
	
	
	public void setSelfPrefix(String prefix) {
		Permissions.backendWriter.setSelfPrefix(name, type, prefix);
	}

	
	public String getSuffix() {
		return Permissions.resolver.getEffectiveSuffix(name, type);
	}
	
	public String getSelfSuffix() {
		return getBackendEntity().getSelfSuffix();
	}
	
	
	public DisplayTreeNode debugSuffix() {
		return Permissions.resolver.debugSuffix(name, type);
	}
	
	
	public void setSelfSuffix(String suffix) {
		Permissions.backendWriter.setSelfSuffix(name, type, suffix);
	}
	
	
	public Map<String, Boolean> listEffectivePermissions() {
		return Permissions.resolver.getEffectivePermissionList(name, type, null, null);
	}
	
	public Map<String, Boolean> listEffectivePermissions(String server) {
		return Permissions.resolver.getEffectivePermissionList(name, type, server, null);
	}
	
	public Map<String, Boolean> listEffectivePermissions(String server, String world) {
		return Permissions.resolver.getEffectivePermissionList(name, type, server, world);
	}
	
	
	public Boolean hasPermission(String permission) {
		return Permissions.resolver.getEffectivePermission(name, type, permission, null, null);
	}
	
	public Boolean hasPermission(String permission, String server) {
		return Permissions.resolver.getEffectivePermission(name, type, permission, server, null);
	}
	
	public Boolean hasPermission(String permission, String server, String world) {
		return Permissions.resolver.getEffectivePermission(name, type, permission, server, world);
	}
	
	
	public DisplayTreeNode debugPermission(String permission) {
		return Permissions.resolver.debugPermission(name, type, permission, null, null);
	}
	
	public DisplayTreeNode debugPermission(String permission, String server) {
		return Permissions.resolver.debugPermission(name, type, permission, server, null);
	}
	
	public DisplayTreeNode debugPermission(String permission, String server, String world) {
		return Permissions.resolver.debugPermission(name, type, permission, server, world);
	}
	
	
	public void addSelfPermission(String permission) {
		Permissions.backendWriter.addSelfPermission(name, type, permission, null, null);
	}
	
	public void addSelfPermission(String permission, String server) {
		Permissions.backendWriter.addSelfPermission(name, type, permission, server, null);
	}
	
	public void addSelfPermission(String permission, String server, String world) {
		Permissions.backendWriter.addSelfPermission(name, type, permission, server, world);
	}
	
	
	public void removeSelfPermission(String permission) {
		Permissions.backendWriter.removeSelfPermission(name, type, permission, null, null);
	}
	
	public void removeSelfPermission(String permission, String server) {
		Permissions.backendWriter.removeSelfPermission(name, type, permission, server, null);
	}
	
	public void removeSelfPermission(String permission, String server, String world) {
		Permissions.backendWriter.removeSelfPermission(name, type, permission, server, world);
	}
	
	public int getSelfPermissionsCount() {
		return getSelfPermissionsServerWorldKeys().stream()
				.mapToInt(key -> getSelfPermissions(key.server, key.world).size())
				.sum();
	}
	
	public Set<ServerWorldKey> getSelfPermissionsServerWorldKeys() {
		return getBackendEntity().getSelfPermissionsServerWorldKeys();
	}
	
	public List<String> getSelfPermissions() {
		return getBackendEntity().getSelfPermissions(null, null);
	}
	
	public List<String> getSelfPermissions(String server) {
		return getBackendEntity().getSelfPermissions(server, null);
	}
	
	public List<String> getSelfPermissions(String server, String world) {
		return getBackendEntity().getSelfPermissions(server, world);
	}
	
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof PermEntity))
			return false;
		PermEntity o = (PermEntity) obj;
		return Objects.equals(name, o.name) && type == o.type;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, type);
	}
	
}