package fr.pandacube.lib.permissions;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.LongStream;

import fr.pandacube.lib.chat.ChatTreeNode;
import fr.pandacube.lib.permissions.PermissionExpressionParser.LiteralPermissionTester;
import fr.pandacube.lib.permissions.PermissionsCachedBackendReader.CachedEntity;
import fr.pandacube.lib.permissions.SQLPermissions.EntityType;
import fr.pandacube.lib.util.Log;

/**
 * Represents an entity in the permission system, either a group or a player.
 */
public sealed abstract class PermEntity permits PermPlayer, PermGroup {
	/* package */ final String name;
	/* package */ final EntityType type;
	/* package */ PermEntity(String n, EntityType t) {
		name = n; type = t;
	}
	
	/* package */ abstract CachedEntity getBackendEntity();

	/**
	 * Gets all the groups this entity inherits from.
	 * @return a list of all the groups this entity inherits from.
	 */
	public abstract List<PermGroup> getInheritances();
	/**
	 * Gets all the group names this entity inherits from.
	 * @return a list of all the group names this entity inherits from.
	 */
	public abstract List<String> getInheritancesString();
	/**
	 * Gets the name of this entity.
	 * @return the name of this entity.
	 */
	public abstract String getName();

	/**
	 * Gets the name of this entity, as it is stored in the database.
	 * @return the name of this entity, as it is stored in the database.
	 */
	public String getInternalName() {
		return name;
	}
	




	/**
	 * Tells if the current entity inherits directly or indirectly from the specified group.
	 * @param group the group to search for
	 * @param recursive true to search in the inheritance tree, or false to search only in the inheritance list of the current entity.
	 * @return true if the current entity inherits directly or indirectly from the specified group, false otherwise.
	 */
	public boolean inheritsFromGroup(String group, boolean recursive) {
		if (group == null)
			return false;
		return getInheritances().stream().anyMatch(g -> g.name.equals(group) || (recursive && g.inheritsFromGroup(group, true)));
	}





	/**
	 * Gets the effective prefix of this entity.
	 * It is either the prefix defined directly for this entity, or from inheritance.
	 * @return the effective prefix of this entity.
	 */
	public String getPrefix() {
		return Permissions.resolver.getEffectivePrefix(name, type);
	}

	/**
	 * Gets the prefix defined directly for this entity.
	 * @return the prefix defined directly for this entity.
	 */
	public String getSelfPrefix() {
		return getBackendEntity().getSelfPrefix();
	}

	/**
	 * Provides information on how the effective prefix of this entity is determined.
	 * @return a {@link ChatTreeNode} providing information on how the effective prefix of this entity is determined.
	 */
	public ChatTreeNode debugPrefix() {
		return Permissions.resolver.debugPrefix(name, type);
	}

	/**
	 * Sets the prefix of this entity.
	 * @param prefix the prefix for this entity.
	 */
	public void setSelfPrefix(String prefix) {
		Permissions.backendWriter.setSelfPrefix(name, type, prefix);
	}





	/**
	 * Gets the effective suffix of this entity.
	 * It is either the suffix defined directly for this entity, or from inheritance.
	 * @return the effective suffix of this entity.
	 */
	public String getSuffix() {
		return Permissions.resolver.getEffectiveSuffix(name, type);
	}

	/**
	 * Gets the suffix defined directly for this entity.
	 * @return the suffix defined directly for this entity.
	 */
	public String getSelfSuffix() {
		return getBackendEntity().getSelfSuffix();
	}

	/**
	 * Provides information on how the effective suffix of this entity is determined.
	 * @return a {@link ChatTreeNode} providing information on how the effective suffix of this entity is determined.
	 */
	public ChatTreeNode debugSuffix() {
		return Permissions.resolver.debugSuffix(name, type);
	}

	/**
	 * Sets the suffix of this entity.
	 * @param suffix the suffix for this entity.
	 */
	public void setSelfSuffix(String suffix) {
		Permissions.backendWriter.setSelfSuffix(name, type, suffix);
	}






	/**
	 * Gets the effective list of permissions that applies to this entity out of a specific server and world.
	 * It is either the permissions defined directly for this entity, or from inheritance as long as they are not
	 * overridden.
	 * @return the effective list of permissions that applies to this entity out of a specific server and world.
	 */
	public Map<String, Boolean> listEffectivePermissions() {
		return listEffectivePermissions(null, null);
	}

	/**
	 * Gets the effective list of permissions that applies to this entity on a specific server.
	 * It is either the permissions defined directly for this entity, or from inheritance as long as they are not
	 * overridden.
	 * @param server the server where the returned permissions apply for this entity.
	 * @return the effective list of permissions that applies to this entity on a specific server.
	 */
	public Map<String, Boolean> listEffectivePermissions(String server) {
		return listEffectivePermissions(server, null);
	}

	/**
	 * Gets the effective list of permissions that applies to this entity on a specific server and world.
	 * It is either the permissions defined directly for this entity, or from inheritance as long as they are not
	 * overridden.
	 * @param server the server containing the world where the returned permissions apply for this entity.
	 * @param world the world in the server where the returned permissions apply for this entity.
	 * @return the effective list of permissions that applies to this entity on a specific server and world.
	 */
	public Map<String, Boolean> listEffectivePermissions(String server, String world) {
		return Permissions.resolver.getEffectivePermissionList(name, type, server, world);
	}







	/**
	 * Gets the effective values of the provided permission range prefix that applies to this entity out of a specific
	 * server and world.
	 * It is either the range values defined directly for this entity, or from inheritance as long as they are not
	 * overridden.
	 * @param permissionPrefix the permission range prefix.
	 * @return the effective values of the provided permission range prefix that applies to this entity out of a
	 *         specific server and world.
	 */
	public LongStream getPermissionRangeValues(String permissionPrefix) {
		return getPermissionRangeValues(permissionPrefix, null, null);
	}

	/**
	 * Gets the effective values of the provided permission range prefix that applies to this entity on a specific
	 * server.
	 * It is either the range values defined directly for this entity, or from inheritance as long as they are not
	 * overridden.
	 * @param permissionPrefix the permission range prefix.
	 * @param server the server where the returned values apply for this entity.
	 * @return the effective values of the provided permission range prefix that applies to this entity on a specific
	 *         server.
	 */
	public LongStream getPermissionRangeValues(String permissionPrefix, String server) {
		return getPermissionRangeValues(permissionPrefix, server, null);
	}

	/**
	 * Gets the effective values of the provided permission range prefix that applies to this entity on a specific
	 * server and world.
	 * It is either the range values defined directly for this entity, or from inheritance as long as they are not
	 * overridden.
	 * @param permissionPrefix the permission range prefix.
	 * @param server the server containing the world where the returned values apply for this entity.
	 * @param world the world in the server where the returned values apply for this entity.
	 * @return the effective values of the provided permission range prefix that applies to this entity on a specific
	 *         server and world.
	 */
	public LongStream getPermissionRangeValues(String permissionPrefix, String server, String world) {
		String prefixWithEndingDot = permissionPrefix.endsWith(".") ? permissionPrefix : (permissionPrefix + ".");
		int prefixLength = prefixWithEndingDot.length();
		return listEffectivePermissions(server, world).entrySet().stream()
				.filter(Map.Entry::getValue) // permission must be positive
				.map(Map.Entry::getKey) // keep only the permission node (key), since the value is always true
				.filter(p -> p.startsWith(prefixWithEndingDot)) // keep only relevant permissions
				.map(p -> p.substring(prefixLength)) // keep only what is after the prefix
				.map(suffix -> { // convert to long
					try {
						return Long.parseLong(suffix);
					}
					catch (NumberFormatException e) {
						return null;
					}
				})
				.filter(Objects::nonNull)
				.mapToLong(longSuffix -> longSuffix)
				.sorted();
	}


	/**
	 * Gets the maximum effective value of the provided permission range prefix that applies to this entity out of a
	 * specific server and world.
	 * It is either the range values defined directly for this entity, or from inheritance as long as they are not
	 * overridden.
	 * @param permissionPrefix the permission range prefix.
	 * @return the maximum effective value of the provided permission range prefix that applies to this entity out of a
	 *         specific server and world.
	 */
	public OptionalLong getPermissionRangeMax(String permissionPrefix) {
		return getPermissionRangeMax(permissionPrefix, null, null);
	}

	/**
	 * Gets the maximum effective value of the provided permission range prefix that applies to this entity on a
	 * specific server.
	 * It is either the range values defined directly for this entity, or from inheritance as long as they are not
	 * overridden.
	 * @param permissionPrefix the permission range prefix.
	 * @param server the server where the returned value applies for this entity.
	 * @return the maximum effective value of the provided permission range prefix that applies to this entity on a
	 *         specific server.
	 */
	public OptionalLong getPermissionRangeMax(String permissionPrefix, String server) {
		return getPermissionRangeMax(permissionPrefix, server, null);
	}

	/**
	 * Gets the maximum effective value of the provided permission range prefix that applies to this entity on a
	 * specific server and world.
	 * It is either the range values defined directly for this entity, or from inheritance as long as they are not
	 * overridden.
	 * @param permissionPrefix the permission range prefix.
	 * @param server the server containing the world where the returned value applies for this entity.
	 * @param world the world in the server where the returned value applies for this entity.
	 * @return the maximum effective value of the provided permission range prefix that applies to this entity on a
	 *         specific server and world.
	 */
	public OptionalLong getPermissionRangeMax(String permissionPrefix, String server, String world) {
		return getPermissionRangeValues(permissionPrefix, server, world).max();
	}







	/**
	 * Tells if this entity has the provided permission out of a specific server and world.
	 * It is either based on the permissions defined directly for this entity, or from inheritance as long as they are
	 * not overridden.
	 * @param permission the permission to check on this entity.
	 * @return true if this entity has the permission, false if it is negated, or null if not known.
	 */
	public Boolean hasPermission(String permission) {
		return hasPermission(permission, null, null);
	}

	/**
	 * Tells if this entity has the provided permission on a specific server.
	 * It is either based on the permissions defined directly for this entity, or from inheritance as long as they are
	 * not overridden. It also considers permissions that apply on any server.
	 * @param permission the permission to check on this entity.
	 * @param server the server in which to test the permission for this entity.
	 * @return true if this entity has the permission, false if it is negated, or null if not known.
	 */
	public Boolean hasPermission(String permission, String server) {
		return hasPermission(permission, server, null);
	}

	/**
	 * Tells if this entity has the provided permission on a specific server and world.
	 * It is either based on the permissions defined directly for this entity, or from inheritance as long as they are
	 * not overridden. It also considers permissions that apply on any world of that server, and then any server.
	 * @param permission the permission to check on this entity.
	 * @param server the server in which to test the permission for this entity.
	 * @param world the world in which to test the permission for this entity.
	 * @return true if this entity has the permission, false if it is negated, or null if not known.
	 */
	public Boolean hasPermission(String permission, String server, String world) {
		Boolean ret = Permissions.resolver.getEffectivePermission(name, type, permission, server, world);
		Log.debug("[Perm] For " + type.toString().toLowerCase() + " " + getName() + ", '" + permission + "' is " + ret);
		return ret;
	}

	/**
	 * Tells if this entity has the provided permission on a specific server and world.
	 * It is either based on the permissions defined directly for this entity, or from inheritance as long as they are
	 * not overridden.
	 * @param permission the permission to check on this entity.
	 * @param server the server in which to test the permission for this entity.
	 * @param world the world in which to test the permission for this entity.
	 * @param deflt the default value is the permission is undefined for this entity.
	 * @return true if this entity has the permission, false if it is negated, or {@code deflt} if not known.
	 */
	public boolean hasPermissionOr(String permission, String server, String world, boolean deflt) {
		Boolean ret = hasPermission(permission, server, world);
		return ret != null ? ret : deflt;
	}

	/**
	 * Evaluates the provided permission expression for this entity.
	 * It uses {@link #hasPermissionOr(String, String, String, boolean)} with {@code false} as a default value, to check
	 * each permission nodes individually.
	 * @param permExpression the permission expression to evaluate on this entity.
	 * @param server the server in which to test the permission expression for this entity.
	 * @param world the world in which to test the permission expression for this entity.
	 * @return true if this the permission expression evaluates to true, false otherwise.
	 * @see PermissionExpressionParser#evaluate(String, LiteralPermissionTester)
	 */
	public boolean hasPermissionExpression(String permExpression, String server, String world) {
		return PermissionExpressionParser.evaluate(permExpression, p -> hasPermissionOr(p, server, world, false));
	}









	/**
	 * Provides information on how the effective permission of this entity on the provided permission node is
	 * determined.
	 * @param permission the permission node to debug on this entity.
	 * @return a {@link ChatTreeNode} providing information on how the effective permission is determined.
	 */
	public ChatTreeNode debugPermission(String permission) {
		return debugPermission(permission, null, null);
	}

	/**
	 * Provides information on how the effective permission of this entity on the provided permission node is
	 * determined.
	 * @param permission the permission node to debug on this entity.
	 * @param server the server in which to test the permission for this entity.
	 * @return a {@link ChatTreeNode} providing information on how the effective permission is determined.
	 */
	public ChatTreeNode debugPermission(String permission, String server) {
		return debugPermission(permission, server, null);
	}

	/**
	 * Provides information on how the effective permission of this entity on the provided permission node is
	 * determined.
	 * @param permission the permission node to debug on this entity.
	 * @param server the server in which to test the permission for this entity.
	 * @param world the world in which to test the permission for this entity.
	 * @return a {@link ChatTreeNode} providing information on how the effective permission is determined.
	 */
	public ChatTreeNode debugPermission(String permission, String server, String world) {
		return Permissions.resolver.debugPermission(name, type, permission, server, world);
	}









	/**
	 * Adds the provided permission node to this entity that apply on any server.
	 * @param permission the permission node to add.
	 * @throws IllegalStateException if the permission is already set.
	 */
	public void addSelfPermission(String permission) {
		addSelfPermission(permission, null, null);
	}

	/**
	 * Adds the provided permission node to this entity that apply on the provided server.
	 * @param permission the permission node to add.
	 * @param server the server in which to apply the permission.
	 * @throws IllegalStateException if the permission is already set.
	 */
	public void addSelfPermission(String permission, String server) {
		addSelfPermission(permission, server, null);
	}

	/**
	 * Adds the provided permission node to this entity that apply on the provided server and world.
	 * @param permission the permission node to add.
	 * @param server the server in which to apply the permission.
	 * @param world the world in which to apply the permission.
	 * @throws IllegalStateException if the permission is already set.
	 */
	public void addSelfPermission(String permission, String server, String world) {
		Permissions.backendWriter.addSelfPermission(name, type, permission, server, world);
	}







	/**
	 * Removes the provided permission node from this entity that applied on any server.
	 * @param permission the permission node to add.
	 * @throws IllegalStateException if the permission was not set.
	 */
	public void removeSelfPermission(String permission) {
		removeSelfPermission(permission, null, null);
	}

	/**
	 * Removes the provided permission node from this entity that applied on the provided server.
	 * @param permission the permission node to remove.
	 * @param server the server from which to remove the permission.
	 * @throws IllegalStateException if the permission was not set.
	 */
	public void removeSelfPermission(String permission, String server) {
		removeSelfPermission(permission, server, null);
	}

	/**
	 * Removes the provided permission node from this entity that applied on the provided server and world.
	 * @param permission the permission node to remove.
	 * @param server the server from which to remove the permission.
	 * @param world the world from which to remove the permission.
	 * @throws IllegalStateException if the permission was not set.
	 */
	public void removeSelfPermission(String permission, String server, String world) {
		Permissions.backendWriter.removeSelfPermission(name, type, permission, server, world);
	}






	/**
	 * Counts the number of self permission nodes for this entity.
	 * @return the number of self permission nodes for this entity.
	 */
	public int getSelfPermissionsCount() {
		return getSelfPermissionsServerWorldKeys().stream()
				.mapToInt(key -> getSelfPermissions(key.server(), key.world()).size())
				.sum();
	}







	/**
	 * Gets all the server/world attribution that have at least one self permission for this entity.
	 * @return all the server/world attribution that have at least one self permission for this entity.
	 */
	public Set<ServerWorldKey> getSelfPermissionsServerWorldKeys() {
		return getBackendEntity().getSelfPermissionsServerWorldKeys();
	}







	/**
	 * Gets all the self permission nodes that apply everywhere for this entity.
	 * @return all the self permission nodes that apply everywhere for this entity.
	 */
	public List<String> getSelfPermissions() {
		return getSelfPermissions(null, null);
	}

	/**
	 * Gets all the self permission nodes that apply on the provided server for this entity.
	 * @param server the server from which to get the permissions.
	 * @return all the self permission nodes that apply on the provided server for this entity.
	 */
	public List<String> getSelfPermissions(String server) {
		return getSelfPermissions(server, null);
	}

	/**
	 * Gets all the self permission nodes that apply on the provided server and world for this entity.
	 * @param server the server from which to get the permissions.
	 * @param world the world from which to get the permissions.
	 * @return all the self permission nodes that apply on the provided server and world for this entity.
	 */
	public List<String> getSelfPermissions(String server, String world) {
		return getBackendEntity().getSelfPermissions(server, world);
	}
	





	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof PermEntity o
				&& Objects.equals(name, o.name)
				&& type == o.type;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, type);
	}
	
}