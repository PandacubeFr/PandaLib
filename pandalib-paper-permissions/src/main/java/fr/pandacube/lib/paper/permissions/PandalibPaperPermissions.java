package fr.pandacube.lib.paper.permissions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Predicate;

import fr.pandacube.lib.db.DB;
import fr.pandacube.lib.db.DBConnection;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.java.JavaPlugin;

import fr.pandacube.lib.permissions.Permissions;
import fr.pandacube.lib.util.Log;

/**
 * Class that integrates the {@code pandalib-permissions} system into a Bukkit/Spigot/Paper instance.
 * The integration is made when calling {@link #onLoad(JavaPlugin, String)} and {@link #onEnable()}.
 * The permission system must be initialized first, using {@link Permissions#init(Function)}.
 * Don’t forget that the permission system also needs a connection to a database, so don’t forget to call
 * {@link DB#init(DBConnection, String)} with the appropriate parameters before anything.
 */
public class PandalibPaperPermissions implements Listener {

	/* package */ static JavaPlugin plugin;
	/* package */ static String serverName;
	/* package */ static final Map<String, String> permissionMap = new HashMap<>();


	/**
	 * Integrates the {@code pandalib-permissions} system into the Bukkit server, during the loading phase of the plugin.
	 * @param plugin a Bukkit plugin.
	 * @param serverName the name of the current server, used to fetch server specific permissions. Cannot be null.
	 *                   If this server in not in a multiserver configuration, use a dummy server name, like
	 *                   {@code ""} (empty string).
	 */
	public static void onLoad(JavaPlugin plugin, String serverName) {
		PandalibPaperPermissions.plugin = plugin;
		PandalibPaperPermissions.serverName = serverName;
		PermissionsInjectorVault.onLoad();
	}

	/**
	 * Integrates the {@code pandalib-permissions} system into the Bukkit server, during the enabling phase of the plugin.
	 */
	public static void onEnable() {
		PermissionsInjectorBukkit.inject(Bukkit.getConsoleSender());
		PermissionsInjectorVault.onEnable();
		PermissionsInjectorWEPIF.inject();

		Bukkit.getPluginManager().registerEvents(new PandalibPaperPermissions(), plugin);
	}

	/**
	 * Add the provided pair of permission into an internal permission map. This is used when a plugin asks the value of
	 * the sourcePerm, then the permission system actually check for the destPerm.
	 * <p>
	 * This mapping is useful, for instance, when the bukkit dispatcher force the fake vanilla commands to have a
	 * permission starting with {@code minecraft.command.} even if we defined a custom permission in the plugin.
	 * @param sourcePerm the source permission to replace
	 * @param destPerm the replacement permission
	 */
	public static void addPermissionMapping(String sourcePerm, String destPerm) {
		Objects.requireNonNull(sourcePerm, "sourcePerm");
		if (destPerm == null) {
			permissionMap.remove(sourcePerm);
		}
		else {
			permissionMap.put(sourcePerm, destPerm);
		}
	}

	/**
	 * Player login event handler.
	 * @param event the event.
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		Permissions.clearPlayerCache(event.getPlayer().getUniqueId());
		Permissions.precachePlayerAsync(event.getPlayer().getUniqueId());
		
		PermissionsInjectorBukkit.inject(event.getPlayer());
	}


	/**
	 * Player quit event handler.
	 * @param event the event.
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		PermissionsInjectorBukkit.uninject(event.getPlayer());
		Permissions.clearPlayerCache(event.getPlayer().getUniqueId());
	}
	
	
	
	
	/* package */ static final Function<String, List<Permission>> SUPERPERMS_PARENT_PERMISSION_GETTER = childPerm -> Bukkit.getPluginManager()
			.getPermissions()
			.stream()
			.filter(p -> p.getChildren().containsKey(childPerm))
			.toList();
	
	/* package */ static ServerOperator dummyOperator(boolean isOp) {
		return new ServerOperator() {
			@Override public void setOp(boolean op) { }
			@Override public boolean isOp() { return isOp; }
		};
	}
	
	/* package */ static Boolean hasSuperPermsPermission(ServerOperator opable, String permission, Predicate<String> parentPermissionChecker) {
		if (opable instanceof CommandSender sender) {
			Permissible permissible = PermissionsInjectorBukkit.getPermissible(sender);
			if (permissible instanceof PermissionsInjectorBukkit.PandaPermissible pPerm)
				return hasSuperPermsPermission(opable, permission, parentPermissionChecker, pPerm);
		}
		return hasSuperPermsPermission(opable, permission, parentPermissionChecker, null);
	}
	
	/* package */ static Boolean hasSuperPermsPermission(ServerOperator opable, String permission, Predicate<String> parentPermissionChecker, PermissionsInjectorBukkit.PandaPermissible pandaPermissible) {
		
		boolean reversed = permission.startsWith("-");
		if (reversed) {
			permission = permission.substring(1);
		}
		
		boolean defined = false;
		Permission perm = Bukkit.getPluginManager().getPermission(permission);
		if (perm != null) {
			if (perm.getDefault().getValue(opable.isOp()))
    			return !reversed;
			defined = true;
		}
		
		try {
			List<Permission> parents = pandaPermissible != null ? pandaPermissible.superPermsPermissionCache.get(permission) : SUPERPERMS_PARENT_PERMISSION_GETTER.apply(permission);
			
			for (Permission parent : parents) {
				Boolean childValue = parent.getChildren().get(permission);
				if (childValue == null)
					continue;
				boolean parentPerm = parentPermissionChecker.test(parent.getName());
				if (parentPerm == childValue)
					return !reversed;
				defined = true;
			}
		} catch (ExecutionException e) {
			Log.severe("Unable to compute SuperPerms permission", e);
		}
		
		Boolean ret = defined ? reversed : null;
		if (Log.isDebugEnabled()) {
			String name = (opable instanceof CommandSender cs) ? cs.getName() : "unknown entity";
			String actualPerm = permission;
			if (reversed) actualPerm = "-" + permission;
			Log.debug("[SuperPerms] For " + name + ", '" + actualPerm + "' is " + ret);
		}
		return ret;
	}

}
