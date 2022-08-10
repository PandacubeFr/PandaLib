package fr.pandacube.lib.paper.permissions;

import java.util.List;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

import fr.pandacube.lib.permissions.PermPlayer;
import fr.pandacube.lib.permissions.Permissions;
import fr.pandacube.lib.util.Log;

/* package */ class PermissionsInjectorWEPIF {
	
	public static void inject() {
		try {
			PandaWEPIFPermissionsProvider permInstance = new PandaWEPIFPermissionsProvider();
			Bukkit.getServicesManager().register(com.sk89q.wepif.PermissionsProvider.class, permInstance,
					PandalibPaperPermissions.plugin, ServicePriority.Highest);
			Log.info("Providing permissions through WEPIF");
			Plugin pl = Bukkit.getPluginManager().getPlugin("WorldEdit");
			if (pl == null || !pl.isEnabled())
				return;
			((WorldEditPlugin) pl).getPermissionsResolver().findResolver();
		} catch (NoClassDefFoundError e) {
			Log.warning("WorldEdit plugin not detected. Not using WEPIF to provide permissions and prefix/suffix." + e.getMessage());
		}
	}





	/* package */ static class PandaWEPIFPermissionsProvider implements com.sk89q.wepif.PermissionsProvider {
		private PandaWEPIFPermissionsProvider() { }
		
		private PermPlayer getPlayer(OfflinePlayer player) {
			return Permissions.getPlayer(player.getUniqueId());
		}
		
		@Override
		public String[] getGroups(OfflinePlayer player) {
			List<String> groups = getPlayer(player).getGroupsString();
			return groups.toArray(new String[0]);
		}
		@Deprecated
		@Override
		public String[] getGroups(String player) {
			return getGroups(Bukkit.getOfflinePlayer(player));
		}
		@Override
		public boolean hasPermission(OfflinePlayer player, String permission) {
			Player p = Bukkit.getPlayer(player.getUniqueId());
			return hasPermission(p != null ? p.getWorld().getName() : null, player, permission);
		}
		@Deprecated
		@Override
		public boolean hasPermission(String player, String permission) {
			return hasPermission(Bukkit.getOfflinePlayer(player), permission);
		}
		@Override
		public boolean hasPermission(String worldName, OfflinePlayer player, String permission) {
			Boolean res = Permissions.getPlayer(player.getUniqueId()).hasPermission(permission, PandalibPaperPermissions.serverName, worldName);
			if (res != null)
				return res;

        	res = PandalibPaperPermissions.hasSuperPermsPermission(player, permission, p -> hasPermission(worldName, player, p));
        	if (res != null)
        		return res;
        	
			return permission.startsWith("-");
		}
		@Deprecated
		@Override
		public boolean hasPermission(String worldName, String player, String permission) {
			return hasPermission(worldName, Bukkit.getOfflinePlayer(player), permission);
		}
		@Override
		public boolean inGroup(OfflinePlayer player, String group) {
			return getPlayer(player).isInGroup(group);
		}
		@Deprecated
		@Override
		public boolean inGroup(String player, String group) {
			return inGroup(Bukkit.getOfflinePlayer(player), group);
		}
	}
}
