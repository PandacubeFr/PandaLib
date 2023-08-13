package fr.pandacube.lib.permissions;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import fr.pandacube.lib.db.DB;
import fr.pandacube.lib.db.DBConnection;
import fr.pandacube.lib.db.DBException;
import fr.pandacube.lib.util.Log;

/**
 * Main class for the Pandalib permission system.
 * <p>
 * This permission system uses the Pandalib DB API to connect to the database, so the connection to the MySQL must be
 * established first, using {@link DB#init(DBConnection, String)}.
 * Then, this class must be initialized using {@link #init(Function)}.
 */
public class Permissions {
	
	/* package */ static PermissionsCachedBackendReader backendReader;
	/* package */ static PermissionsResolver resolver;
	/* package */ static PermissionsBackendWriter backendWriter;
	/* package */ static Function<UUID, String> playerNameGetter = UUID::toString;
	
	/**
	 * Initialize the permission system.
	 * The connection to the database needs to be initialized first, using {@link DB#init(DBConnection, String)}.
	 * @param playerNameGetter a function to get the player name associated with a UUID. It is used for
	 *                         and to generate {@link PermPlayer#getName()} and for
	 *                         {@link PermEntity#debugPermission(String)}.
	 * @throws DBException if an error occurs when interacting with the database.
	 */
	public static void init(Function<UUID, String> playerNameGetter) throws DBException {
		Permissions.playerNameGetter = playerNameGetter == null ? UUID::toString : playerNameGetter;
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

	private static void checkInitialized() {
		if (backendReader == null) {
			throw new IllegalStateException("Permissions system not initialized. Check the server logs to check if there is an error during the startup, and check if the init() method is called properly.");
		}
	}

	/**
	 * Adds the provided special permissions to this permission system.
	 * @param specialPermissions the {@link SpecialPermission}s to add.
	 * @throws IllegalStateException if the permission system was not initialized properly.
	 */
	public static void addSpecialPermissions(SpecialPermission... specialPermissions) {
		checkInitialized();
		if (specialPermissions == null)
			return;
		resolver.specialPermissions.addAll(Arrays.asList(specialPermissions));
	}

	/**
	 * Clears the cached data of a specific player.
	 * @param playerId the UUID of the player.
	 * @throws IllegalStateException if the permission system was not initialized properly.
	 */
	public static void clearPlayerCache(UUID playerId) {
		checkInitialized();
		backendReader.clearPlayerCache(playerId);
		resolver.clearPlayerFromCache(playerId);
	}

	/**
	 * Clears all the cached data (players and groups) and fetch all the groups' data from the database.
	 * The clearing and fetching of the data is made asynchronously in a new thread.
	 * @param then the action to perform after the cache has been updated.
	 * @throws IllegalStateException if the permission system was not initialized properly.
	 */
	public static void clearCache(Runnable then) {
		checkInitialized();
		backendReader.clearAndResetCacheAsync(() -> {
			resolver.clearCache();
			if (then != null)
				then.run();
		});
	}

	/**
	 * Gets the permission player object.
	 * @param playerId the UUID of the player.
	 * @return the permission player object.
	 * @throws IllegalStateException if the permission system was not initialized properly.
	 */
	public static PermPlayer getPlayer(UUID playerId) {
		checkInitialized();
		return new PermPlayer(playerId);
	}

	/**
	 * Gets a dummy permission player object, that have no specific data, only inheriting from the default groups.
	 *
	 * The current implementation provides a player named {@code default.0} with an uuid of
	 * {@code fffdef17-ffff-b0ff-ffff-ffffffffffff}.
	 * Trying to set a permission data for this player will log a warning.
	 * @return the default permission player.
	 */
	public static PermPlayer getDefaultPlayer() {
		checkInitialized();
		return new DefaultPlayer();
	}

	/**
	 * Asks the permission system to preventively and asynchronously cache the data of the provided player.
	 * This can be called as soon as possible when a player connects, so the permission data of the player are
	 * accessible as soon as possible when they are needed.
	 * @param playerId the UUID of the player.
	 * @throws IllegalStateException if the permission system was not initialized properly.
	 */
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

	/**
	 * Gets the permission group object.
	 * @param name the name of the group.
	 * @return the permission group object.
	 * @throws IllegalStateException if the permission system was not initialized properly.
	 */
	public static PermGroup getGroup(String name) {
		checkInitialized();
		return new PermGroup(name);
	}

	/**
	 * Gets all the permission group objects.
	 * @return all the permission group objects.
	 * @throws IllegalStateException if the permission system was not initialized properly.
	 */
	public static List<PermGroup> getGroups() {
		checkInitialized();
		return PermGroup.fromCachedGroups(backendReader.getGroups());
	}

	/**
	 * Gets all the default permission group objects.
	 * @return all the default permission group objects.
	 * @throws IllegalStateException if the permission system was not initialized properly.
	 */
	public static List<PermGroup> getDefaultGroups() {
		checkInitialized();
		return PermGroup.fromCachedGroups(backendReader.getDefaultGroups());
	}

	/**
	 * Gets the full permission list.
	 * @return the full permission list.
	 * @throws IllegalStateException if the permission system was not initialized properly.
	 */
	public static List<String> getFullPermissionsList() {
		checkInitialized();
		return backendReader.getFullPermissionsList();
	}
	
}