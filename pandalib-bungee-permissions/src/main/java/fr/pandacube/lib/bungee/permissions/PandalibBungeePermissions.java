package fr.pandacube.lib.bungee.permissions;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import fr.pandacube.lib.permissions.Permissions;
import fr.pandacube.lib.players.permissible.PermissibleOnlinePlayer;
import fr.pandacube.lib.players.permissible.PermissiblePlayerManager;

public class PandalibBungeePermissions implements Listener {

    public static void init(Plugin bungeePlugin) {
        ProxyServer.getInstance().getPluginManager().registerListener(bungeePlugin, new PandalibBungeePermissions());
    }


    @EventHandler(priority = Byte.MAX_VALUE)
    public void onPermissionCheck(PermissionCheckEvent event)
    {
        CommandSender s = event.getSender();
        if (s instanceof ProxiedPlayer p) {
            event.setHasPermission(hasPerm(p, event.getPermission()));
        }
        else {
            event.setHasPermission(true);
        }
    }

    private volatile boolean tryPermPlayerManager = true;

    private boolean hasPerm(ProxiedPlayer p, String permission) {
        if (tryPermPlayerManager) {
            try {
                PermissiblePlayerManager<?, ?> pm = PermissiblePlayerManager.getInstance();
                if (pm != null) {
                    PermissibleOnlinePlayer op = pm.get(p.getUniqueId());
                    if (op != null) {
                        return op.hasPermission(permission);
                    }
                }
            } catch (NoClassDefFoundError ignored) {
                tryPermPlayerManager = false;
            }
        }

        // if not using player manager, fallback to directly call permissions API
        Server sv = p.getServer();
        String server = sv == null ? null : sv.getInfo().getName();
        return Permissions.getPlayer(p.getUniqueId()).hasPermissionOr(permission, server, null, false);
    }
}
