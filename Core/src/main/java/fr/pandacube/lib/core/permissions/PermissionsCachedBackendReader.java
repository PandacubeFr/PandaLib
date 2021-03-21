package fr.pandacube.lib.core.permissions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import fr.pandacube.lib.core.db.DB;
import fr.pandacube.lib.core.db.DBException;
import fr.pandacube.lib.core.db.SQLElementList;
import fr.pandacube.lib.core.permissions.SQLPermissions.EntityType;
import fr.pandacube.lib.core.util.Log;

/* package */ class PermissionsCachedBackendReader
{
	/* package */ PermissionsCachedBackendReader() throws DBException {
		clearAndResetCache();
	}
	
	
	
	
	private Cache<UUID, CachedPlayer> usersCache = CacheBuilder.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build();
	private Set<String> fullPermissionsList = new TreeSet<String>();
	
	/* package */ synchronized List<String> getFullPermissionsList() {
		return new ArrayList<>(fullPermissionsList);
	}
	
	/* package */ synchronized void clearPlayerCache(UUID playerId) {
		usersCache.invalidate(playerId);
	}
	
	/* package */ synchronized CachedPlayer getCachedPlayer(UUID playerId) {
		try {
			return usersCache.get(playerId, () -> {
				try {
					return initPlayer(playerId);
				} catch (DBException e) {
					throw new RuntimeException(e);
				}
			});
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
	
	private CachedPlayer initPlayer(UUID playerId) throws DBException {
		
		SQLElementList<SQLPermissions> playerData = DB.getAll(SQLPermissions.class,
				SQLPermissions.type.eq(EntityType.User.getCode())
						.and(SQLPermissions.name.like(playerId.toString()))
				);
		
		Map<String, List<SQLPermissions>> playerRawData = playerData.stream()
				.collect(
						Collectors.groupingBy(e -> e.get(SQLPermissions.key),
								LinkedHashMap::new,
								Collectors.toList())
						);
		
		String playerSelfPrefix = null;
		if (playerRawData.containsKey("prefix")) {
			playerSelfPrefix = playerRawData.get("prefix").stream()
					.map(e -> e.get(SQLPermissions.value))
					.collect(Collectors.joining());
		}
				
		String playerSelfSuffix = null;
		if (playerRawData.containsKey("suffix")) {
			playerSelfSuffix = playerRawData.get("suffix").stream()
					.map(e -> e.get(SQLPermissions.value))
					.collect(Collectors.joining());
		}
		
		Map<ServerWorldKey, List<String>> playerSelfPerms = new LinkedHashMap<>();
		if (playerRawData.containsKey("permissions")) {
			playerSelfPerms = playerRawData.get("permissions").stream()
					.peek(e -> {
						String value = e.get(SQLPermissions.value);
						fullPermissionsList.add(value.substring(value.startsWith("-") ? 1 : 0));
					})
					.collect(Collectors.groupingBy(e -> new ServerWorldKey(e.get(SQLPermissions.server), e.get(SQLPermissions.world)),
									LinkedHashMap::new,
									Collectors.mapping(e -> e.get(SQLPermissions.value),
											Collectors.toList()
											)
									)
							);
		}
		
		CachedPlayer player = new CachedPlayer(playerId, playerSelfPrefix, playerSelfSuffix, playerSelfPerms);
		
		if (playerRawData.containsKey("groups")) {
			playerRawData.get("groups").stream()
					.map(e -> e.get(SQLPermissions.value))
					.forEach(g -> {
						player.groups.add(getCachedGroup(g));
					});
		}
		
		if (player.groups.isEmpty()) {
			player.usingDefaultGroups = true;
			getDefaultGroups().forEach(player.groups::add);
		}
		
		return player;
	}
	
	
	
	
	private Map<String, CachedGroup> groupsCache = new LinkedHashMap<>();
	private boolean cacheIsUpdating = false;
	
	
	/* package */ synchronized CachedGroup getCachedGroup(String group) {
		return groupsCache.getOrDefault(group, new CachedGroup(group, null, null, false, new LinkedHashMap<>()));
	}
	
	/* package */ synchronized List<CachedGroup> getDefaultGroups() {
		return groupsCache.values().stream()
				.filter(g -> g.deflt)
				.collect(Collectors.toList());
	}

	public List<CachedGroup> getGroups() {
		return new ArrayList<>(groupsCache.values());
	}
	
	/* package */ void clearAndResetCacheAsync(Runnable then) {
		synchronized (this) {
			if (cacheIsUpdating)
				return;
		}
		Thread t = new Thread(() -> {
			try {
				clearAndResetCache();
			} catch (Throwable e) {
				Log.severe(e);
			}
			if (then != null)
				then.run();
		}, "Permissions Backend Group Cache Updater");
		t.setDaemon(true);
		t.start();
	}
	
	private void clearAndResetCache() throws DBException {
		synchronized (this) {
			if (cacheIsUpdating)
				return;
			cacheIsUpdating = true;
		}
		
		try {
			Map<String, CachedGroup> newData = new LinkedHashMap<>();
			Set<String> newFullPermissionsList = new TreeSet<>();
			
			SQLElementList<SQLPermissions> groupData = DB.getAll(SQLPermissions.class, SQLPermissions.type.eq(EntityType.Group.getCode()));
			
			Map<String, Map<String, List<SQLPermissions>>> groupsRawData = groupData.stream()
					.collect(
							Collectors.groupingBy(e -> e.get(SQLPermissions.name),
									LinkedHashMap::new,
									Collectors.groupingBy(e -> e.get(SQLPermissions.key),
											LinkedHashMap::new,
											Collectors.toList())
									)
							);
			
			for (String groupName : groupsRawData.keySet()) {
				initGroup(groupName, groupsRawData, newData, newFullPermissionsList);
			}
			
			synchronized (this) {
				groupsCache.clear();
				groupsCache.putAll(newData);
				cacheIsUpdating = false;
				usersCache.invalidateAll();
				fullPermissionsList = newFullPermissionsList;
			}
		} finally {
			synchronized (this) {
				cacheIsUpdating = false;
			}
		}
		
		
	}
	
	private void initGroup(String groupName, Map<String, Map<String, List<SQLPermissions>>> groupsRawData, Map<String, CachedGroup> newData, Set<String> newFullPermissionsList) {
		if (newData.containsKey(groupName))
			return;
		
		Map<String, List<SQLPermissions>> groupRawData = groupsRawData.getOrDefault(groupName, new LinkedHashMap<>());

		boolean groupDefault = groupRawData.containsKey("default")
				? "true".equals(groupRawData.get("default").get(0).get(SQLPermissions.value))
				: false;
		
		String groupSelfPrefix = null;
		if (groupRawData.containsKey("prefix")) {
			groupSelfPrefix = groupRawData.get("prefix").stream()
					.map(e -> e.get(SQLPermissions.value))
					.collect(Collectors.joining());
		}
				
		String groupSelfSuffix = null;
		if (groupRawData.containsKey("suffix")) {
			groupSelfSuffix = groupRawData.get("suffix").stream()
					.map(e -> e.get(SQLPermissions.value))
					.collect(Collectors.joining());
		}
		
		Map<ServerWorldKey, List<String>> groupSelfPerms = new LinkedHashMap<>();
		if (groupRawData.containsKey("permissions")) {
			groupSelfPerms = groupRawData.get("permissions").stream()
					.peek(e -> {
						String value = e.get(SQLPermissions.value);
						newFullPermissionsList.add(value.substring(value.startsWith("-") ? 1 : 0));
					})
					.collect(Collectors.groupingBy(e -> new ServerWorldKey(e.get(SQLPermissions.server), e.get(SQLPermissions.world)),
									LinkedHashMap::new,
									Collectors.mapping(e -> e.get(SQLPermissions.value),
											Collectors.toList()
											)
									)
							);
		}
		
		CachedGroup group = new CachedGroup(groupName, groupSelfPrefix, groupSelfSuffix, groupDefault, groupSelfPerms);
		
		newData.put(groupName, group);
		
		
		if (groupRawData.containsKey("inheritances")) {
			groupRawData.get("inheritances").stream()
					.map(e -> e.get(SQLPermissions.value))
					.forEach(g -> {
						initGroup(g, groupsRawData, newData, newFullPermissionsList);
						group.inheritances.add(newData.get(g));
					});
		}
	}
	
	
	/* package */ static abstract class CachedEntity {
		public final String name;
		private final String selfPrefix, selfSuffix;
		private final Map<ServerWorldKey, List<String>> selfPermissions;
		
		private CachedEntity(String n, String p, String s,
				Map<ServerWorldKey, List<String>> perms) {
			name = n; selfPrefix = p; selfSuffix = s; selfPermissions = perms;
		}
		
		/* package */ List<String> getSelfPermissions(String server, String world) {
			return selfPermissions.getOrDefault(new ServerWorldKey(server, world), new ArrayList<>());
		}
		
		/* package */ Set<ServerWorldKey> getSelfPermissionsServerWorldKeys() {
			return new TreeSet<>(selfPermissions.keySet());
		}
		
		/* package */ String getSelfPrefix() {
			return selfPrefix;
		}
		
		/* package */ String getSelfSuffix() {
			return selfSuffix;
		}
	}
	
	/* package */ static class CachedPlayer extends CachedEntity {
		public final UUID playerId;
		public final List<CachedGroup> groups = new ArrayList<>();
		public boolean usingDefaultGroups = false;
		private CachedPlayer(UUID pl, String p, String s,
				Map<ServerWorldKey, List<String>> perms) {
			super(pl.toString(), p, s, perms);
			playerId = pl;
		}
	}
	
	/* package */ static class CachedGroup extends CachedEntity {
		public final boolean deflt;
		public final List<CachedGroup> inheritances = new ArrayList<>();
		private CachedGroup(String n, String p, String s,
				boolean dflt, Map<ServerWorldKey, List<String>> perms) {
			super(n, p, s, perms);
			deflt = dflt;
		}
	}
    
}
