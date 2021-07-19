package fr.pandacube.lib.core.permissions;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import fr.pandacube.lib.core.chat.Chat;
import fr.pandacube.lib.core.chat.ChatUtil;
import fr.pandacube.lib.core.chat.ChatUtil.DisplayTreeNode;
import fr.pandacube.lib.core.permissions.PermissionsCachedBackendReader.CachedEntity;
import fr.pandacube.lib.core.permissions.PermissionsCachedBackendReader.CachedGroup;
import fr.pandacube.lib.core.permissions.PermissionsCachedBackendReader.CachedPlayer;
import fr.pandacube.lib.core.permissions.SQLPermissions.EntityType;
import fr.pandacube.lib.core.players.PlayerFinder;
import fr.pandacube.lib.core.util.Log;
import net.md_5.bungee.api.ChatColor;

public class PermissionsResolver {

	private PermissionsCachedBackendReader backendReader;
	
	/* package */ PermissionsResolver(PermissionsCachedBackendReader b) {
		backendReader = b;
	}
	
	/* package */ void clearPlayerFromCache(UUID player) {
		String playerId = player.toString();
		synchronized (effectivePermissionsCache) {
			effectivePermissionsCache.asMap().keySet().removeIf(k -> k.type == EntityType.User && playerId.equals(k.name));
		}
		synchronized (effectivePermissionsListCache) {
			effectivePermissionsListCache.asMap().keySet().removeIf(k -> k.type == EntityType.User && playerId.equals(k.name));
		}
		synchronized (effectiveDataCache) {
			effectiveDataCache.asMap().keySet().removeIf(k -> k.type == EntityType.User && playerId.equals(k.name));
		}
	}
	
	/* package */ void clearCache() {
		effectivePermissionsCache.invalidateAll();
		effectivePermissionsListCache.invalidateAll();
		effectiveDataCache.invalidateAll();
	}
	
	
	
	
	


	/* package */ String getEffectivePrefix(String name, EntityType type) {
		return getEffectiveData(name, type, DataType.PREFIX);
	}
	/* package */ String getEffectiveSuffix(String name, EntityType type) {
		return getEffectiveData(name, type, DataType.SUFFIX);
	}
	
	/* package */ DisplayTreeNode debugPrefix(String name, EntityType type) {
		return debugData(name, type, DataType.PREFIX);
	}
	/* package */ DisplayTreeNode debugSuffix(String name, EntityType type) {
		return debugData(name, type, DataType.SUFFIX);
	}

	private Cache<DataCacheKey, String> effectiveDataCache = CacheBuilder.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build();
	
	private String getEffectiveData(String name, EntityType type, DataType dataType) {
		Objects.requireNonNull(name, "name can’t be null");
		Objects.requireNonNull(type, "type can’t be null");
		
		try {
			return effectiveDataCache.get(new DataCacheKey(name, type, dataType), () -> {
				return resolveData(name, type, dataType);
			});
		} catch (ExecutionException e) {
			Log.severe(e);
			return null;
		}
	}
	
	private DisplayTreeNode debugData(String name, EntityType type, DataType dataType) {
		CachedEntity entity = (type == EntityType.User)
				? backendReader.getCachedPlayer(UUID.fromString(name))
				: backendReader.getCachedGroup(name);
		return resolveData(entity, dataType).toDisplayTreeNode();
	}
	
	private String resolveData(String name, EntityType type, DataType dataType) {
		CachedEntity entity = (type == EntityType.User)
				? backendReader.getCachedPlayer(UUID.fromString(name))
				: backendReader.getCachedGroup(name);
		DataResolutionNode resolutionResult = resolveData(entity, dataType);
		
		if (resolutionResult.conflict) {
			Log.warning("For data " + dataType + ":");
			for (Chat cmp : ChatUtil.treeView(resolutionResult.toDisplayTreeNode(), true))
				Log.warning(cmp.getLegacyText());
		}
		
		return resolutionResult.result != null ? resolutionResult.result : "";
	}
	
	private DataResolutionNode resolveData(CachedEntity entity, DataType dataType) {
		// self data
		DataResolutionNode resolutionNode = new DataResolutionNode(entity, dataType.getter.apply(entity), null);
		if (resolutionNode.result != null) {
			return resolutionNode;
		}
		
		// check inheritances data
		List<CachedGroup> inheritances = resolutionNode.entity instanceof CachedPlayer
				? ((CachedPlayer)resolutionNode.entity).groups
				: ((CachedGroup)resolutionNode.entity).inheritances;
		
		List<DataResolutionNode> inheritedResults = new ArrayList<>(inheritances.size());
		
		for (CachedGroup inherited : inheritances) {
			inheritedResults.add(resolveData(inherited, dataType));
		}
		
		resolutionNode.inheritances.addAll(inheritedResults);
		
		if (inheritedResults.stream().anyMatch(g -> g.conflict))
			resolutionNode.conflict = true;
		
		Set<String> inheritedPermissions = inheritedResults.stream()
				.map(g -> g.result)
				.filter(r -> r != null)
				.collect(Collectors.toSet());
		
		if (inheritedPermissions.size() == 1)
			resolutionNode.result = inheritedPermissions.iterator().next();
		else if (inheritedPermissions.size() > 1) {
			resolutionNode.conflictMessage = (resolutionNode.conflictMessage == null ? "" : (resolutionNode.conflictMessage + " ; "))
					+ "Unsolvable conflict between inherited groups";
			resolutionNode.conflict = true;
		}
		
		return resolutionNode;
	}
	

	private static class DataResolutionNode {
		final CachedEntity entity;
		String result;
		String conflictMessage;
		boolean conflict;
		final List<DataResolutionNode> inheritances = new ArrayList<>();
		public DataResolutionNode(CachedEntity e, String r, String c) {
			entity = e; result = r; conflictMessage = c;
			conflict = c != null;
		}
		
		public DisplayTreeNode toDisplayTreeNode() {
			Chat c = Chat.text(entity.name);
			if (result == null)
				c.then(Chat.text(" (non défini)").gray());
			else
				c.thenLegacyText(" \"" + ChatColor.RESET + result + ChatColor.RESET + "\"");
			if (conflictMessage != null)
				c.thenFailure(" " + conflictMessage);
			DisplayTreeNode node = new DisplayTreeNode(c);
			
			if (result == null && conflict == false && !inheritances.isEmpty()) {
				// there is nothing interesting to show on current or subnode
				node.children.add(new DisplayTreeNode(Chat.text("(Inheritances hidden for brevety)").darkGray().italic()));
				return node;
			}
			
			inheritances.forEach(n -> node.children.add(n.toDisplayTreeNode()));
			
			return node;
		}
	}
	
	private static class DataCacheKey {
		final String name;
		final EntityType type;
		final DataType dataType;
		DataCacheKey(String n, EntityType t, DataType d) {
			name = n; type = t; dataType = d;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof DataCacheKey))
				return false;
			DataCacheKey o = (DataCacheKey) obj;
			return Objects.equals(name, o.name)
					&& Objects.equals(type, o.type)
					&& dataType == o.dataType;
		}
	}
	
	private enum DataType {
		PREFIX(CachedEntity::getSelfPrefix),
		SUFFIX(CachedEntity::getSelfSuffix);
		
		private final CachedEntityGetter<String> getter;
		private DataType(CachedEntityGetter<String> g) {
			getter = g;
		}
	}
	
	private interface CachedEntityGetter<R> {
		R apply(CachedEntity a);
	}
	
	
	
	
	
	
	
	
	
	
	
	

	
	private Cache<PermCacheKey, Map<String, Boolean>> effectivePermissionsListCache = CacheBuilder.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build();
	
	/* package */ Map<String, Boolean> getEffectivePermissionList(String name, EntityType type, String server, String world) {
		Objects.requireNonNull(name, "name can’t be null");
		Objects.requireNonNull(type, "type can’t be null");
		Preconditions.checkArgument(world == null || server != null, "world not null but server is null");
		
		String fServer = server == null ? null : server.toLowerCase();
		String fWorld = world == null ? null : world.toLowerCase();
		
		try {
			return effectivePermissionsListCache.get(new PermCacheKey(name, type, null, fServer, fWorld), () -> {
				Map<String, Boolean> permList = new LinkedHashMap<>();
				
				for (String perm : backendReader.getFullPermissionsList()) {
					Boolean has = getEffectivePermission(name, type, perm, fServer, fWorld);
					if (has == null)
						continue;
					permList.put(perm.toLowerCase(), has);
				}
				
				return permList;
			});
		} catch (ExecutionException e) {
			Log.severe(e);
			return null;
		}
		
	}

	private Cache<PermCacheKey, PermState> effectivePermissionsCache = CacheBuilder.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build();
	
	/* package */ Boolean getEffectivePermission(String name, EntityType type, String permission, String server, String world) {
		Objects.requireNonNull(name, "name can’t be null");
		Objects.requireNonNull(type, "type can’t be null");
		Objects.requireNonNull(permission, "permission can’t be null");
		Preconditions.checkArgument(world == null || server != null, "world not null but server is null");
		
		boolean reversed = false;
		if (permission.startsWith("-")) {
			permission = permission.substring(1);
			reversed = true;
		}
		
		String fPermission = permission == null ? null : permission.toLowerCase();
		String fServer = server == null ? null : server.toLowerCase();
		String fWorld = world == null ? null : world.toLowerCase();
		try {
			Boolean resolved = effectivePermissionsCache.get(new PermCacheKey(name, type, fPermission, fServer, fWorld), () -> {
				return resolvePermission(name, type, fPermission, fServer, fWorld);
			}).value;
			return resolved == null ? null : (reversed != resolved.booleanValue());
		} catch (ExecutionException e) {
			Log.severe(e);
			return null;
		}
	}
	
	/* package */ DisplayTreeNode debugPermission(String name, EntityType type, String permission, String server, String world) {
		CachedEntity entity = (type == EntityType.User)
				? backendReader.getCachedPlayer(UUID.fromString(name))
				: backendReader.getCachedGroup(name);
		return resolvePermission(entity, permission, server, world, true).toDisplayTreeNode();
	}
	
	private PermState resolvePermission(String name, EntityType type, String permission, String server, String world) {
		
		CachedEntity entity = (type == EntityType.User)
				? backendReader.getCachedPlayer(UUID.fromString(name))
				: backendReader.getCachedGroup(name);
		PermResolutionNode resolutionResult = resolvePermission(entity, permission, server, world, true);
		
		if (resolutionResult.conflict) {
			Log.warning("For permission " + permission + ":");
			for (Chat cmp : ChatUtil.treeView(resolutionResult.toDisplayTreeNode(), true))
				Log.warning(cmp.getLegacyText());
		}
		
		return resolutionResult.result;
	}
	
	
	
	
	
	
	private PermResolutionNode resolvePermission(CachedEntity entity, String permission, String server, String world, boolean checkInheritance) {
		
		// self and special permissions
		PermResolutionNode resolutionNode = resolveSelfPermission(entity, permission, server, world);
		if (resolutionNode.result != PermState.UNDEFINED) {
			return resolutionNode;
		}
		
		List<CachedGroup> inheritances = resolutionNode.entity instanceof CachedPlayer
				? ((CachedPlayer)resolutionNode.entity).groups
				: ((CachedGroup)resolutionNode.entity).inheritances;

				
		// check no-world/no-server permissions
		if (server != null) {
			PermResolutionNode noWNoSNode = resolvePermission(entity, permission, world != null ? server : null, null, false);
			resolutionNode.inheritances.add(noWNoSNode);
			if (noWNoSNode.conflict)
				resolutionNode.conflict = true;
			if (noWNoSNode.result != PermState.UNDEFINED) {
				resolutionNode.result = noWNoSNode.result;
				return resolutionNode;
			}
		}
		
		
		if (!checkInheritance)
			return resolutionNode;
		
		// check inheritances permissions
		List<PermResolutionNode> inheritedResults = new ArrayList<>(inheritances.size() + 1);
		
		for (CachedGroup inherited : inheritances) {
			inheritedResults.add(resolvePermission(inherited, permission, server, world, true));
		}
		
		resolutionNode.inheritances.addAll(inheritedResults);
		
		if (inheritedResults.stream().anyMatch(g -> g.conflict))
			resolutionNode.conflict = true;
		
		Set<PermState> inheritedPermissions = inheritedResults.stream()
				.map(g -> g.result)
				.collect(Collectors.toCollection(() -> EnumSet.noneOf(PermState.class)));
		
		boolean inheritancesGranted = inheritedPermissions.contains(PermState.GRANTED);
		boolean inheritancesRevoqued = inheritedPermissions.contains(PermState.REVOQUED);
		if (inheritancesGranted != inheritancesRevoqued) {
			resolutionNode.result = inheritancesGranted ? PermState.GRANTED : PermState.REVOQUED;
		}
		else if (inheritancesGranted && inheritancesRevoqued) {
			resolutionNode.conflictMessage = (resolutionNode.conflictMessage == null ? "" : (resolutionNode.conflictMessage + " ; "))
					+ "Unsolvable conflict between inheritances";
			resolutionNode.conflict = true;
		}
		
		return resolutionNode;
	}
	
	
	
	
	private PermResolutionNode resolveSelfPermission(CachedEntity entity, String permission, String server, String world) {
		// special permissions
		PermState result = PermState.UNDEFINED;
		String conflict = null;
		List<ParsedSelfPermission> foundPerms = null;
		
		/*
		 * Check for special permissions
		 */
		if (entity instanceof CachedPlayer) {
			PermPlayer permP = new PermPlayer(((CachedPlayer) entity).playerId);
			ParsedSelfPermission specialPerm = null;
			if (permission.equals("pandacube.grade.isinstaff")) {
				boolean res = permP.inheritsFromGroup("staff-base", true);
				specialPerm = new ParsedSelfPermission(permission, res, PermType.SPECIAL);
				conflict = "Special permission 'pandacube.grade.isinstaff' is deprecated. Use 'pandacube.inheritsfrom.<staffBaseGroup>' instead.";
			}
			else if (permission.startsWith("pandacube.grade.")) {
				String group = permission.substring("pandacube.grade.".length());
				boolean res = permP.inheritsFromGroup(group, false);
				specialPerm = new ParsedSelfPermission(permission, res, PermType.SPECIAL);
				conflict = "Special permission 'pandacube.grade.<groupName>' is deprecated. Use 'pandacube.ingroup.<groupName>' instead.";
			}
			else if (permission.startsWith("pandacube.ingroup.")) {
				String group = permission.substring("pandacube.ingroup.".length());
				boolean res = permP.inheritsFromGroup(group, false);
				specialPerm = new ParsedSelfPermission(permission, res, PermType.SPECIAL);
			}
			else if (permission.startsWith("pandacube.inheritsfrom.")) {
				String group = permission.substring("pandacube.inheritsfrom.".length());
				boolean res = permP.inheritsFromGroup(group, true);
				specialPerm = new ParsedSelfPermission(permission, res, PermType.SPECIAL);
			}
			else if (permission.startsWith("pandacube.inserver.")) {
				String testedServer = permission.substring("pandacube.inserver.".length());
				boolean res = testedServer.equals(server);
				specialPerm = new ParsedSelfPermission(permission, res, PermType.SPECIAL);
			}
			else if (permission.startsWith("pandacube.inserverworld.")) {
				String testedServerWorld = permission.substring("pandacube.inserverworld.".length());
				boolean res = server != null && world != null && testedServerWorld.equals(server + "." + world);
				specialPerm = new ParsedSelfPermission(permission, res, PermType.SPECIAL);
			}
			
			if (specialPerm != null) {
				result = PermState.of(specialPerm.result);
				foundPerms = new ArrayList<>();
				foundPerms.add(specialPerm);
			}
		}
		
		
		
		if (result == PermState.UNDEFINED) {
			foundPerms = entity.getSelfPermissions(server, world).stream()
					.map(p -> {
						ParsedSelfPermission resNode = null;
						if (p.equalsIgnoreCase(permission))
							resNode = new ParsedSelfPermission(p, true, PermType.EXPLICIT);
						else if (p.equalsIgnoreCase("-" + permission))
							resNode = new ParsedSelfPermission(p, false, PermType.EXPLICIT);
						else if (p.endsWith("*") && permission.startsWith(p.substring(0, p.length() - 1)))
							resNode = new ParsedSelfPermission(p, true, PermType.WILDCARD);
						else if (p.endsWith("*") && p.startsWith("-") && permission.startsWith(p.substring(1, p.length() - 1)))
							resNode = new ParsedSelfPermission(p, false, PermType.WILDCARD);
						return resNode;
					})
					.filter(p -> p != null)
					.collect(Collectors.toList());

				boolean explicitGranted = foundPerms.stream()
						.anyMatch(n -> n.type == PermType.EXPLICIT && n.result == Boolean.TRUE);
				boolean explicitRevoqued = foundPerms.stream()
						.anyMatch(n -> n.type == PermType.EXPLICIT && n.result == Boolean.FALSE);
				
				boolean wildcardGranted = foundPerms.stream()
						.anyMatch(n -> n.type == PermType.WILDCARD && n.result == Boolean.TRUE);
				boolean wildcardRevoqued = foundPerms.stream()
						.anyMatch(n -> n.type == PermType.WILDCARD && n.result == Boolean.FALSE);
				
				if (explicitGranted != explicitRevoqued) {
					result = PermState.of(explicitGranted);
					if (!wildcardGranted && !wildcardRevoqued) { }
					else if (wildcardGranted && wildcardRevoqued) {
						conflict = "Self explicit permission defined but conflict between self wildcard permissions";
					}
					else if (explicitGranted == wildcardGranted) {
						conflict = "Unnecessary explicit permission already granted by self wildcard permissions"; // redundent explicit perm
					}
				}
				else if (explicitGranted && explicitRevoqued) {
					conflict = "Unsolvable conflit between explicit permissions";
				}
				else if (wildcardGranted != wildcardRevoqued) {
					result = PermState.of(wildcardGranted);
				}
				else if (wildcardGranted && wildcardRevoqued) {
					conflict = "Unsolvable conflit between wildcard permissions";
				}
		}
		
		PermResolutionNode node = new PermResolutionNode(entity, server, world, result, conflict);
		node.selfPermissions = foundPerms;
		
		return node;
	}
	
	private static class PermResolutionNode {
		final CachedEntity entity;
		final String server, world;
		PermState result;
		String conflictMessage;
		boolean conflict;
		List<ParsedSelfPermission> selfPermissions = new ArrayList<>();
		final List<PermResolutionNode> inheritances = new ArrayList<>();
		public PermResolutionNode(CachedEntity e, String s, String w, PermState r, String c) {
			entity = e; server = s; world = w; result = r; conflictMessage = c;
			conflict = c != null;
		}
		
		public DisplayTreeNode toDisplayTreeNode() {
			Chat c = Chat.chat()
					.then(result == PermState.UNDEFINED ? Chat.dataText("■") : result == PermState.GRANTED ? Chat.successText("✔") : Chat.failureText("✘"))
					.then(Chat.text(entity instanceof CachedPlayer ? PlayerFinder.getLastKnownName(((CachedPlayer)entity).playerId) : entity.name)
							.color(entity instanceof CachedPlayer ? ChatColor.GOLD : ChatColor.DARK_AQUA)
					);
			if (server != null)
				c.thenData(" s=" + server);
			if (world != null)
				c.thenData(" w=" + world);
			if (conflictMessage != null)
				c.then(Chat.failureText(" " + conflictMessage));
			DisplayTreeNode node = new DisplayTreeNode(c);
			
			selfPermissions.forEach(p -> node.children.add(p.toDisplayTreeNode()));
			
			if (result == PermState.UNDEFINED && conflict == false && !inheritances.isEmpty()) {
				// there is nothing interesting to show on current or subnode
				node.children.add(new DisplayTreeNode(Chat.text("(Inheritances hidden for brevety)").darkGray().italic()));
				return node;
			}
			
			inheritances.forEach(n -> node.children.add(n.toDisplayTreeNode()));
			
			return node;
		}
	}
	
	private static class ParsedSelfPermission {
		final String permission;
		final boolean result;
		final PermType type;
		public ParsedSelfPermission(String p, boolean r, PermType t) {
			permission = p;
			result = r;
			type = t;
		}
		public DisplayTreeNode toDisplayTreeNode() {
			return new DisplayTreeNode(Chat.chat()
					.then(result ? Chat.successText("✔") : Chat.failureText("✘"))
					.then(Chat.text(permission).color(type == PermType.WILDCARD ? ChatColor.YELLOW : type == PermType.SPECIAL ? ChatColor.LIGHT_PURPLE : ChatColor.WHITE)));
		}
	}
	
	private static class PermCacheKey {
		final String name;
		final EntityType type;
		final String permission, server, world;
		public PermCacheKey(String n, EntityType t, String p, String s, String w) {
			name = n; type = t; permission = p; server = s; world = w;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof PermCacheKey))
				return false;
			PermCacheKey o = (PermCacheKey) obj;
			return Objects.equals(name, o.name)
					&& Objects.equals(type, o.type)
					&& Objects.equals(permission, o.permission)
					&& Objects.equals(server, o.server)
					&& Objects.equals(world, o.world);
		}
	}
	
	private enum PermType {
		EXPLICIT, WILDCARD, SPECIAL;
	}
	
	private enum PermState {
		GRANTED(true),
		REVOQUED(false),
		UNDEFINED(null);
		final Boolean value;
		private PermState(Boolean v) { value = v; }
		private static PermState of(Boolean v) {
			return v == null ? UNDEFINED : v ? GRANTED : REVOQUED;
		}
	}
	
}
