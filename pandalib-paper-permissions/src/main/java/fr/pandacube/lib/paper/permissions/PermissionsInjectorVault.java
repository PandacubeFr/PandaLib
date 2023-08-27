package fr.pandacube.lib.paper.permissions;

import fr.pandacube.lib.permissions.PermGroup;
import fr.pandacube.lib.permissions.Permissions;
import fr.pandacube.lib.util.log.Log;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.ServicePriority;

import java.util.List;

/* package */ class PermissionsInjectorVault {

	private static final ServicePriority servicePriority = ServicePriority.Highest;
	
	public static PandaVaultPermission permInstance;

	/**
	 * Vault injection needs to happen as soon as possible so other plugins detects it when they load.
	 */
	public static void onLoad() {
		try {
			permInstance = new PandaVaultPermission();
			PandaVaultChat chat = new PandaVaultChat(permInstance);
			Bukkit.getServicesManager().register(Permission.class, permInstance,
					PandalibPaperPermissions.plugin, servicePriority);
			Bukkit.getServicesManager().register(Chat.class, chat,
					PandalibPaperPermissions.plugin, servicePriority);
			Log.info("Providing permissions and chat prefix/suffix through Vault API.");
		} catch (NoClassDefFoundError e) {
			Log.warning("Vault plugin not detected. Not using it to provide permissions and prefix/suffix." + e.getMessage());
		}
	}
	
	public static void onEnable() {
		Bukkit.getScheduler().runTaskLater(PandalibPaperPermissions.plugin,
				PermissionsInjectorVault::checkServicesRegistration, 1);
	}


	private static void checkServicesRegistration() {
		Permission permService = Bukkit.getServicesManager().load(Permission.class);
		if (!(permService instanceof PandaVaultPermission)) {
			Log.severe("Check for Vault Permission service failed. "
					+ (permService == null ? "Service manager returned null."
					: ("Returned service is " + permService.getName() + " (" + permService.getClass().getName() + ").")));

		}
		Chat chatService = Bukkit.getServicesManager().load(Chat.class);
		if (!(chatService instanceof PandaVaultChat)) {
			Log.severe("Check for Vault Chat service failed. "
					+ (chatService == null ? "Service manager returned null."
					: ("Returned service is " + chatService.getName() + " (" + chatService.getClass().getName() + ").")));
		}
	}





	/* package */ static class PandaVaultPermission extends Permission {
		
		private PandaVaultPermission() { }

		@Override
		public String getName() {
			return "Pandalib";
		}

		@Override
		public boolean isEnabled() {
			return PandalibPaperPermissions.plugin != null && PandalibPaperPermissions.plugin.isEnabled();
		}

		private void checkEnabled() {
			if (!isEnabled())
				throw new IllegalStateException("Cannot provide permission service because plugin is disabled.");
		}

		@Override
		public boolean hasSuperPermsCompat() {
			return true;
		}

		@Deprecated
		@Override
		public boolean playerHas(String world, String player, String permission) {
			return playerHas(world, Bukkit.getOfflinePlayer(player), permission);
		}
		
		@Override
		public boolean playerHas(String world, OfflinePlayer player, String permission) {
			checkEnabled();
			Boolean res = Permissions.getPlayer(player.getUniqueId()).hasPermission(permission, PandalibPaperPermissions.serverName, world);
			if (res != null)
				return res;

        	res = PandalibPaperPermissions.hasSuperPermsPermission(player, permission, p -> playerHas(world, player, p));
        	if (res != null)
        		return res;
        	
			return permission.startsWith("-");
		}

		@Deprecated
		@Override
		public boolean playerAdd(String world, String player, String permission) {
			return playerAdd(world, Bukkit.getOfflinePlayer(player), permission);
		}

		@Override
		public boolean playerAdd(String world, OfflinePlayer player, String permission) {
			checkEnabled();
			String server = PandalibPaperPermissions.serverName;
			Permissions.getPlayer(player.getUniqueId()).addSelfPermission(permission, server, world);
			Log.info("A plugin added permission " + permission + " (server=" + server + ",world=" + world + ") to player " + player.getName() + " through Vault.");
			return true;
		}

		@Deprecated
		@Override
		public boolean playerRemove(String world, String player, String permission) {
			return playerRemove(world, Bukkit.getOfflinePlayer(player), permission);
		}

		@Override
		public boolean playerRemove(String world, OfflinePlayer player, String permission) {
			checkEnabled();
			String server = PandalibPaperPermissions.serverName;
			Permissions.getPlayer(player.getUniqueId()).removeSelfPermission(permission, server, world);
			Log.info("A plugin removed permission " + permission + " (server=" + server + ",world=" + world + ") to player " + player.getName() + " through Vault.");
			return true;
		}

		@Override
		public boolean groupHas(String world, String group, String permission) {
			checkEnabled();
			Boolean res = Permissions.getGroup(group).hasPermission(permission, PandalibPaperPermissions.serverName, world);
			if (res != null)
				return res;
			
        	res = PandalibPaperPermissions.hasSuperPermsPermission(PandalibPaperPermissions.dummyOperator(false), permission, p -> groupHas(world, group, p), null);
        	if (res != null)
        		return res;
        	
			return permission.startsWith("-");
		}

		@Override
		public boolean groupAdd(String world, String group, String permission) {
			Log.warning(new Throwable("A plugin tried to add to group " + group + " (world=" + world + ") the permission " + permission
					+ " through Vault but Pandalib does not support it."));
			return false;
		}

		@Override
		public boolean groupRemove(String world, String group, String permission) {
			Log.warning(new Throwable("A plugin tried to remove from group " + group + " (world=" + world + ") the permission " + permission
					+ " through Vault but Pandalib does not support it."));
			return false;
		}

		@Deprecated
		@Override
		public boolean playerInGroup(String world, String player, String group) {
			return playerInGroup(world, Bukkit.getOfflinePlayer(player), group);
		}
		
		@Override
		public boolean playerInGroup(String world, OfflinePlayer player, String group) {
			checkEnabled();
			return Permissions.getPlayer(player.getUniqueId()).isInGroup(group);
		}

		@Deprecated
		@Override
		public boolean playerAddGroup(String world, String player, String group) {
			Log.warning(new Throwable("A plugin tried to add player " + player + " (world=" + world + ") to permission group " + group
					+ " through Vault but Pandalib does not support it."));
			return false;
		}

		@Deprecated
		@Override
		public boolean playerRemoveGroup(String world, String player, String group) {
			Log.warning(new Throwable("A plugin tried to remove player " + player + " (world=" + world + ") from permission group " + group
					+ " through Vault but Pandalib does not support it."));
			return false;
		}

		@Deprecated
		@Override
		public String[] getPlayerGroups(String world, String player) {
			return getPlayerGroups(world, Bukkit.getOfflinePlayer(player));
		}
		
		@Override
		public String[] getPlayerGroups(String world, OfflinePlayer player) {
			checkEnabled();
			List<String> groups = Permissions.getPlayer(player.getUniqueId()).getGroupsString();
			return groups.toArray(new String[0]);
		}

		@Deprecated
		@Override
		public String getPrimaryGroup(String world, String player) {
			return getPrimaryGroup(world, Bukkit.getOfflinePlayer(player));
		}
		
		@Override
		public String getPrimaryGroup(String world, OfflinePlayer player) {
			checkEnabled();
			return Permissions.getPlayer(player.getUniqueId()).getGroupsString().stream()
					.findFirst().orElse(null);
		}

		@Override
		public String[] getGroups() {
			checkEnabled();
			return Permissions.getGroups().stream()
					.map(PermGroup::getName).toArray(String[]::new);
		}

		@Override
		public boolean hasGroupSupport() {
			return true;
		}
		
	}
	
	
	private static class PandaVaultChat extends Chat {

		public PandaVaultChat(Permission perms) {
			super(perms);
		}

		@Override
		public String getName() {
			return "Pandalib";
		}

		@Override
		public boolean isEnabled() {
			return PandalibPaperPermissions.plugin != null && PandalibPaperPermissions.plugin.isEnabled();
		}

		private void checkEnabled() {
			if (!isEnabled())
				throw new IllegalStateException("Cannot provide permission service because plugin is disabled.");
		}

		@Deprecated
		@Override
		public String getPlayerPrefix(String world, String player) {
			return getPlayerPrefix(world, Bukkit.getOfflinePlayer(player));
		}
		
		@Override
		public String getPlayerPrefix(String world, OfflinePlayer player) {
			checkEnabled();
			return Permissions.getPlayer(player.getUniqueId()).getPrefix();
		}

		@Deprecated
		@Override
		public String getPlayerSuffix(String world, String player) {
			return getPlayerSuffix(world, Bukkit.getOfflinePlayer(player));
		}
		
		@Override
		public String getPlayerSuffix(String world, OfflinePlayer player) {
			checkEnabled();
			return Permissions.getPlayer(player.getUniqueId()).getSuffix();
		}

		@Override
		public String getGroupPrefix(String world, String group) {
			checkEnabled();
			return Permissions.getGroup(group).getPrefix();
		}

		@Override
		public String getGroupSuffix(String world, String group) {
			checkEnabled();
			return Permissions.getGroup(group).getSuffix();
		}

		@Deprecated
		@Override
		public void setPlayerPrefix(String world, String player, String prefix) { /* unsupported */ }
		@Deprecated
		@Override
		public void setPlayerSuffix(String world, String player, String suffix) { /* unsupported */ }
		@Override
		public void setGroupPrefix(String world, String group, String prefix) { /* unsupported */ }
		@Override
		public void setGroupSuffix(String world, String group, String suffix) { /* unsupported */ }
		@Deprecated
		@Override
		public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) { return defaultValue; }
		@Deprecated
		@Override
		public void setPlayerInfoInteger(String world, String player, String node, int value) { /* unsupported */ }
		@Override
		public int getGroupInfoInteger(String world, String group, String node, int defaultValue) { return defaultValue; }
		@Override
		public void setGroupInfoInteger(String world, String group, String node, int value) { /* unsupported */ }
		@Deprecated
		@Override
		public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) { return defaultValue; }
		@Deprecated
		@Override
		public void setPlayerInfoDouble(String world, String player, String node, double value) { /* unsupported */ }
		@Override
		public double getGroupInfoDouble(String world, String group, String node, double defaultValue) { return defaultValue; }
		@Override
		public void setGroupInfoDouble(String world, String group, String node, double value) { /* unsupported */ }
		@Deprecated
		@Override
		public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) { return defaultValue; }
		@Deprecated
		@Override
		public void setPlayerInfoBoolean(String world, String player, String node, boolean value) { /* unsupported */ }
		@Override
		public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) { return defaultValue; }
		@Override
		public void setGroupInfoBoolean(String world, String group, String node, boolean value) { /* unsupported */ }
		@Deprecated
		@Override
		public String getPlayerInfoString(String world, String player, String node, String defaultValue) { return defaultValue; }
		@Deprecated
		@Override
		public void setPlayerInfoString(String world, String player, String node, String value) { /* unsupported */ }
		@Override
		public String getGroupInfoString(String world, String group, String node, String defaultValue) { return defaultValue; }
		@Override
		public void setGroupInfoString(String world, String group, String node, String value) { /* unsupported */ }
	}

}
