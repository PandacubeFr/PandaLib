package fr.pandacube.lib.permissions;

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
import net.md_5.bungee.api.ChatColor;

import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.chat.ChatTreeNode;
import fr.pandacube.lib.permissions.PermissionsCachedBackendReader.CachedEntity;
import fr.pandacube.lib.permissions.PermissionsCachedBackendReader.CachedGroup;
import fr.pandacube.lib.permissions.PermissionsCachedBackendReader.CachedPlayer;
import fr.pandacube.lib.permissions.SQLPermissions.EntityType;
import fr.pandacube.lib.util.log.Log;

/* package */ class PermissionsResolver {

	private final PermissionsCachedBackendReader backendReader;
	
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
	
	/* package */ ChatTreeNode debugPrefix(String name, EntityType type) {
		return debugData(name, type, DataType.PREFIX);
	}
	/* package */ ChatTreeNode debugSuffix(String name, EntityType type) {
		return debugData(name, type, DataType.SUFFIX);
	}

	private final Cache<DataCacheKey, String> effectiveDataCache = CacheBuilder.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build();
	
	private String getEffectiveData(String name, EntityType type, DataType dataType) {
		Objects.requireNonNull(name, "name can’t be null");
		Objects.requireNonNull(type, "type can’t be null");
		
		try {
			return effectiveDataCache.get(new DataCacheKey(name, type, dataType), () -> resolveData(name, type, dataType));
		} catch (ExecutionException e) {
			Log.severe(e);
			return null;
		}
	}
	
	private ChatTreeNode debugData(String name, EntityType type, DataType dataType) {
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
			Log.warning("For data " + dataType + ":\n"
					+ resolutionResult.toDisplayTreeNode().render(true).stream()
					.map(Chat::getLegacyText)
					.collect(Collectors.joining(ChatColor.RESET + "\n")));
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
				.filter(Objects::nonNull)
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
		
		public ChatTreeNode toDisplayTreeNode() {
			Chat c = Chat.text(entity.name);
			if (result == null)
				c.then(Chat.text(" (non défini)").gray());
			else
				c.thenLegacyText(" \"" + ChatColor.RESET + result + ChatColor.RESET + "\"");
			if (conflictMessage != null)
				c.thenFailure(" " + conflictMessage);
			ChatTreeNode node = new ChatTreeNode(c);
			
			if (result == null && !conflict && !inheritances.isEmpty()) {
				// there is nothing interesting to show on current or sub node
				node.children.add(new ChatTreeNode(Chat.text("(Inheritances hidden for brevity)").darkGray().italic()));
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
			return obj instanceof DataCacheKey o
					&& Objects.equals(name, o.name)
					&& type == o.type
					&& dataType == o.dataType;
		}
	}
	
	private enum DataType {
		PREFIX(CachedEntity::getSelfPrefix),
		SUFFIX(CachedEntity::getSelfSuffix);
		
		private final CachedEntityGetter<String> getter;
		DataType(CachedEntityGetter<String> g) {
			getter = g;
		}
	}
	
	private interface CachedEntityGetter<R> {
		R apply(CachedEntity a);
	}
	
	
	
	
	
	
	
	
	
	
	
	

	
	private final Cache<PermCacheKey, Map<String, Boolean>> effectivePermissionsListCache = CacheBuilder.newBuilder()
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

	private final Cache<PermCacheKey, PermState> effectivePermissionsCache = CacheBuilder.newBuilder()
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
		
		String fPermission = permission.toLowerCase();
		String fServer = server == null ? null : server.toLowerCase();
		String fWorld = world == null ? null : world.toLowerCase();
		try {
			Boolean resolved = effectivePermissionsCache.get(new PermCacheKey(name, type, fPermission, fServer, fWorld),
					() -> resolvePermission(name, type, fPermission, fServer, fWorld)
			).value;
			return resolved == null ? null : (reversed != resolved);
		} catch (ExecutionException e) {
			Log.severe(e);
			return null;
		}
	}
	
	/* package */ ChatTreeNode debugPermission(String name, EntityType type, String permission, String server, String world) {
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
			Log.warning("For permission " + permission + ":\n"
					+ resolutionResult.toDisplayTreeNode().render(true).stream()
					.map(Chat::getLegacyText)
					.collect(Collectors.joining(ChatColor.RESET + "\n")));
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
		boolean inheritancesRevoked = inheritedPermissions.contains(PermState.REVOKED);
		if (inheritancesGranted != inheritancesRevoked) {
			resolutionNode.result = inheritancesGranted ? PermState.GRANTED : PermState.REVOKED;
		}
		else if (inheritancesGranted) {
			resolutionNode.conflictMessage = (resolutionNode.conflictMessage == null ? "" : (resolutionNode.conflictMessage + " ; "))
					+ "Unsolvable conflict between inheritances";
			resolutionNode.conflict = true;
		}
		
		return resolutionNode;
	}
	
	
	
	
	
	
	/* package */ final List<SpecialPermission> specialPermissions = new ArrayList<>();
	
	
	
	private PermResolutionNode resolveSelfPermission(CachedEntity entity, String permission, String server, String world) {
		// special permissions
		PermState result = PermState.UNDEFINED;
		String conflict = null;
		List<ParsedSelfPermission> foundPerms = null;
		
		/*
		 * Check for special permissions
		 */
		if (entity instanceof CachedPlayer) {
			PermPlayer permP = Permissions.getPlayer(((CachedPlayer) entity).playerId);
			ParsedSelfPermission specialPerm = null;
			
			for (SpecialPermission spePerm : specialPermissions) {
				if (spePerm.matcher().match(permission)) {
					boolean res = spePerm.tester().test(permP, permission, server, world);
					specialPerm = new ParsedSelfPermission(permission, res, PermType.SPECIAL);
					break;
				}
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
					.filter(Objects::nonNull)
					.collect(Collectors.toList());

				boolean explicitGranted = foundPerms.stream()
						.anyMatch(n -> n.type == PermType.EXPLICIT && n.result == Boolean.TRUE);
				boolean explicitRevoked = foundPerms.stream()
						.anyMatch(n -> n.type == PermType.EXPLICIT && n.result == Boolean.FALSE);
				
				boolean wildcardGranted = foundPerms.stream()
						.anyMatch(n -> n.type == PermType.WILDCARD && n.result == Boolean.TRUE);
				boolean wildcardRevoked = foundPerms.stream()
						.anyMatch(n -> n.type == PermType.WILDCARD && n.result == Boolean.FALSE);
				
				if (explicitGranted != explicitRevoked) {
					result = PermState.of(explicitGranted);
					if (!wildcardGranted && !wildcardRevoked) { }
					else if (wildcardGranted && wildcardRevoked) {
						conflict = "Self explicit permission defined but conflict between self wildcard permissions";
					}
					else if (explicitGranted == wildcardGranted) {
						conflict = "Unnecessary explicit permission already granted by self wildcard permissions"; // redundant explicit perm
					}
				}
				else if (explicitGranted) {
					conflict = "Unsolvable conflit between explicit permissions";
				}
				else if (wildcardGranted != wildcardRevoked) {
					result = PermState.of(wildcardGranted);
				}
				else if (wildcardGranted) {
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
		
		public ChatTreeNode toDisplayTreeNode() {
			Chat c = Chat.chat()
					.then(result == PermState.UNDEFINED ? Chat.dataText("■") : result == PermState.GRANTED ? Chat.successText("✔") : Chat.failureText("✘"))
					.then(Chat.text(entity instanceof CachedPlayer cp ? Permissions.playerNameGetter.apply(cp.playerId) : entity.name)
							.color(entity instanceof CachedPlayer ? ChatColor.GOLD : ChatColor.DARK_AQUA)
					);
			if (server != null)
				c.thenData(" s=" + server);
			if (world != null)
				c.thenData(" w=" + world);
			if (conflictMessage != null)
				c.then(Chat.failureText(" " + conflictMessage));
			ChatTreeNode node = new ChatTreeNode(c);
			
			selfPermissions.forEach(p -> node.children.add(p.toDisplayTreeNode()));
			
			if (result == PermState.UNDEFINED && !conflict && !inheritances.isEmpty()) {
				// there is nothing interesting to show on current or sub node
				node.children.add(new ChatTreeNode(Chat.text("(Inheritances hidden for brevity)").darkGray().italic()));
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
		public ChatTreeNode toDisplayTreeNode() {
			return new ChatTreeNode(Chat.chat()
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
			return obj instanceof PermCacheKey o
					&& type == o.type
					&& Objects.equals(name, o.name)
					&& Objects.equals(permission, o.permission)
					&& Objects.equals(server, o.server)
					&& Objects.equals(world, o.world);
		}
	}
	
	private enum PermType {
		EXPLICIT, WILDCARD, SPECIAL
	}
	
	private enum PermState {
		GRANTED(true),
		REVOKED(false),
		UNDEFINED(null);
		final Boolean value;
		PermState(Boolean v) { value = v; }
		private static PermState of(Boolean v) {
			return v == null ? UNDEFINED : v ? GRANTED : REVOKED;
		}
	}
	
}
