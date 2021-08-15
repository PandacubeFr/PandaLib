package fr.pandacube.lib.core.permissions;

import java.util.List;
import java.util.UUID;

import fr.pandacube.lib.core.db.DB;
import fr.pandacube.lib.core.db.DBException;
import fr.pandacube.lib.core.util.Log;

public class Permissions {
	
	/* package */ static PermissionsCachedBackendReader backendReader;
	/* package */ static PermissionsResolver resolver;
	/* package */ static PermissionsBackendWriter backendWriter;
	
	/**
	 * Initialize the permission system.
	 * The connection to the database needs to be initialized first, using {@link DB#init(DBConnection, String)}.
	 * @throws DBException
	 */
	public static void init() throws DBException {
		if (backendReader != null)
			return;
		try {
			DB.initTable(SQLPermissions.class);
			backendReader = new PermissionsCachedBackendReader();
			resolver = new PermissionsResolver(backendReader);
			backendWriter = new PermissionsBackendWriter();
		} catch (Exception e) {
			backendReader = null;
			resolver = null;
			backendWriter = null;
			throw e;
		}
	}
	
	public static void addSpecialPermissions(SpecialPermission... specialPermissions) {
		checkInitialized();
		if (specialPermissions == null)
			return;
		for (SpecialPermission sp : specialPermissions)
			resolver.specialPermissions.add(sp);
	}

	private static void checkInitialized() {
		if (backendReader == null) {
			throw new IllegalStateException("Permissions system not initialized. Check the server logs to check if there is an error during the startup, and check if the init() method is called properly.");
		}
	}
	
	public static void clearPlayerCache(UUID playerId) {
		checkInitialized();
		backendReader.clearPlayerCache(playerId);
		resolver.clearPlayerFromCache(playerId);
	}
	
	public static void clearCache(Runnable then) {
		checkInitialized();
		backendReader.clearAndResetCacheAsync(() -> {
			resolver.clearCache();
			if (then != null)
				then.run();
		});
	}
	
	
	public static PermPlayer getPlayer(UUID playerId) {
		checkInitialized();
		return new PermPlayer(playerId);
	}
	
	public static void precachePlayerAsync(UUID playerId) {
		checkInitialized();
		Thread t = new Thread(() -> {
			try {
				backendReader.getCachedPlayer(playerId);
			} catch (RuntimeException e) {
				Log.warning("Can’t init player cache asynchronously: " + e.getMessage());
			}
		}, "Async permissions player cache loader");
		t.setDaemon(true);
		t.start();
	}
	
	public static PermGroup getGroup(String name) {
		checkInitialized();
		return new PermGroup(name);
	}
	
	public static List<PermGroup> getGroups() {
		checkInitialized();
		return PermGroup.fromCachedGroups(backendReader.getGroups());
	}
	
	public static List<PermGroup> getDefaultGroups() {
		checkInitialized();
		return PermGroup.fromCachedGroups(backendReader.getDefaultGroups());
	}
	
	public static List<String> getFullPermissionsList() {
		return backendReader.getFullPermissionsList();
	}
	
}