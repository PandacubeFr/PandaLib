package fr.pandacube.lib.paper.permissions;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.ServicePriority;

import fr.pandacube.lib.permissions.PermGroup;
import fr.pandacube.lib.permissions.Permissions;
import fr.pandacube.lib.util.Log;

/* package */ class PermissionsInjectorVault {
	
	public static PandaVaultPermission permInstance;
	
	public static void inject() {
		try {
			permInstance = new PandaVaultPermission();
			PandaVaultChat chat = new PandaVaultChat(permInstance);
			Bukkit.getServicesManager().register(net.milkbowl.vault.permission.Permission.class, permInstance,
					PandalibPaperPermissions.plugin, ServicePriority.High);
			Bukkit.getServicesManager().register(net.milkbowl.vault.chat.Chat.class, chat,
					PandalibPaperPermissions.plugin, ServicePriority.High);
			Log.info("Providing permissions and chat prefix/suffix through Vault API.");
		} catch (NoClassDefFoundError e) {
			Log.warning("Vault plugin not detected. Not using it to provide permissions and prefix/suffix." + e.getMessage());
		}
	}





	/* package */ static class PandaVaultPermission extends net.milkbowl.vault.permission.Permission {
		
		private PandaVaultPermission() { }

		@Override
		public String getName() {
			return "Pandalib";
		}

		@Override
		public boolean isEnabled() {
			return PandalibPaperPermissions.plugin != null && PandalibPaperPermissions.plugin.isEnabled();
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
			Boolean res = Permissions.getPlayer(player.getUniqueId()).hasPermission(permission, PandalibPaperPermissions.serverName, world);
			if (res != null)
				return res;

        	res = PandalibPaperPermissions.hasSuperPermsPermission(player, permission, p -> playerHas(world, player, p));
        	if (res != null)
        		return res;
        	
			return permission.startsWith("-");
		}

		@Override
		public boolean playerAdd(String world, String player, String permission) {
			return false;
		}

		@Override
		public boolean playerRemove(String world, String player, String permission) {
			return false;
		}

		@Override
		public boolean groupHas(String world, String group, String permission) {
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
			return false;
		}

		@Override
		public boolean groupRemove(String world, String group, String permission) {
			return false;
		}

		@Deprecated
		@Override
		public boolean playerInGroup(String world, String player, String group) {
			return playerInGroup(world, Bukkit.getOfflinePlayer(player), group);
		}
		
		@Override
		public boolean playerInGroup(String world, OfflinePlayer player, String group) {
			return Permissions.getPlayer(player.getUniqueId()).isInGroup(group);
		}

		@Override
		public boolean playerAddGroup(String world, String player, String group) {
			return false;
		}

		@Override
		public boolean playerRemoveGroup(String world, String player, String group) {
			return false;
		}

		@Deprecated
		@Override
		public String[] getPlayerGroups(String world, String player) {
			return getPlayerGroups(world, Bukkit.getOfflinePlayer(player));
		}
		
		@Override
		public String[] getPlayerGroups(String world, OfflinePlayer player) {
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
			return Permissions.getPlayer(player.getUniqueId()).getGroupsString().stream()
					.findFirst().orElse(null);
		}

		@Override
		public String[] getGroups() {
			return Permissions.getGroups().stream()
					.map(PermGroup::getName).toArray(String[]::new);
		}

		@Override
		public boolean hasGroupSupport() {
			return true;
		}
		
	}
	
	
	private static class PandaVaultChat extends net.milkbowl.vault.chat.Chat {

		public PandaVaultChat(net.milkbowl.vault.permission.Permission perms) {
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

		@Deprecated
		@Override
		public String getPlayerPrefix(String world, String player) {
			return getPlayerPrefix(world, Bukkit.getOfflinePlayer(player));
		}
		
		@Override
		public String getPlayerPrefix(String world, OfflinePlayer player) {
			return Permissions.getPlayer(player.getUniqueId()).getPrefix();
		}

		@Deprecated
		@Override
		public String getPlayerSuffix(String world, String player) {
			return getPlayerSuffix(world, Bukkit.getOfflinePlayer(player));
		}
		
		@Override
		public String getPlayerSuffix(String world, OfflinePlayer player) {
			return Permissions.getPlayer(player.getUniqueId()).getSuffix();
		}

		@Override
		public String getGroupPrefix(String world, String group) {
			return Permissions.getGroup(group).getPrefix();
		}

		@Override
		public String getGroupSuffix(String world, String group) {
			return Permissions.getGroup(group).getSuffix();
		}

		@Override
		public void setPlayerPrefix(String world, String player, String prefix) { /* unsupported */ }
		@Override
		public void setPlayerSuffix(String world, String player, String suffix) { /* unsupported */ }
		@Override
		public void setGroupPrefix(String world, String group, String prefix) { /* unsupported */ }
		@Override
		public void setGroupSuffix(String world, String group, String suffix) { /* unsupported */ }
		@Override
		public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) { return defaultValue; }
		@Override
		public void setPlayerInfoInteger(String world, String player, String node, int value) { /* unsupported */ }
		@Override
		public int getGroupInfoInteger(String world, String group, String node, int defaultValue) { return defaultValue; }
		@Override
		public void setGroupInfoInteger(String world, String group, String node, int value) { /* unsupported */ }
		@Override
		public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) { return defaultValue; }
		@Override
		public void setPlayerInfoDouble(String world, String player, String node, double value) { /* unsupported */ }
		@Override
		public double getGroupInfoDouble(String world, String group, String node, double defaultValue) { return defaultValue; }
		@Override
		public void setGroupInfoDouble(String world, String group, String node, double value) { /* unsupported */ }
		@Override
		public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) { return defaultValue; }
		@Override
		public void setPlayerInfoBoolean(String world, String player, String node, boolean value) { /* unsupported */ }
		@Override
		public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) { return defaultValue; }
		@Override
		public void setGroupInfoBoolean(String world, String group, String node, boolean value) { /* unsupported */ }
		@Override
		public String getPlayerInfoString(String world, String player, String node, String defaultValue) { return defaultValue; }
		@Override
		public void setPlayerInfoString(String world, String player, String node, String value) { /* unsupported */ }
		@Override
		public String getGroupInfoString(String world, String group, String node, String defaultValue) { return defaultValue; }
		@Override
		public void setGroupInfoString(String world, String group, String node, String value) { /* unsupported */ }
	}

}
