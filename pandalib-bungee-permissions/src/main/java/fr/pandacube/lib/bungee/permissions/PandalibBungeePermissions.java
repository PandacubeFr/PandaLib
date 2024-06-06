package fr.pandacube.lib.bungee.permissions;

import fr.pandacube.lib.db.DB;
import fr.pandacube.lib.db.DBConnection;
import fr.pandacube.lib.permissions.Permissions;
import fr.pandacube.lib.players.standalone.AbstractOnlinePlayer;
import fr.pandacube.lib.players.standalone.AbstractPlayerManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.function.Function;

/**
 * Class that integrates the {@code pandalib-permissions} system into a BungeeCord instance.
 * To register the event listener into BungeeCord, use {@link #init(Plugin)}.
 * The permission system must be initialized first, using {@link Permissions#init(Function)}.
 * Don’t forget that the permission system also needs a connection to a database, so don’t forget to call
 * {@link DB#init(DBConnection, String)} with the appropriate parameters before anything.
 */
public class PandalibBungeePermissions implements Listener {

    /**
     * Registers event listener to redirect permission checks to {@code pandalib-permissions}.
     * @param bungeePlugin a BungeeCord plugin.
     */
    public static void init(Plugin bungeePlugin) {
        ProxyServer.getInstance().getPluginManager().registerListener(bungeePlugin, new PandalibBungeePermissions());
    }


    private PandalibBungeePermissions() {}

    /**
     * Event handler called when a plugin asks if a player has a permission.
     * @param event the permission check event.
     */
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
        String world = null;
        if (tryPermPlayerManager) {
            try {
                AbstractPlayerManager<?, ?> pm = AbstractPlayerManager.getInstance();
                if (pm != null) {
                    AbstractOnlinePlayer op = pm.get(p.getUniqueId());
                    if (op != null) {
                        world = op.getWorldName();
                    }
                }
            } catch (NoClassDefFoundError ignored) {
                tryPermPlayerManager = false;
            }
        }

        // if not using player manager, fallback to directly call permissions API
        Server sv = p.getServer();
        String server = sv == null ? null : sv.getInfo().getName();
        return Permissions.getPlayer(p.getUniqueId()).hasPermissionOr(permission, server, world, false);
    }
}
