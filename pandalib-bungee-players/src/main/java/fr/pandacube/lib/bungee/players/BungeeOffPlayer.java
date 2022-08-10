package fr.pandacube.lib.bungee.players;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import fr.pandacube.lib.players.standalone.AbstractOffPlayer;

/**
 * Represents any player on a Bungeecord proxy, either offline or online.
 */
public interface BungeeOffPlayer extends AbstractOffPlayer {

    /*
     * Related class instances
     */

    /**
     * Returns the {@link ProxiedPlayer} instance of this player, or null if not available (offline).
     * @return the {@link ProxiedPlayer} instance of this player, or null if not available (offline).
     */
    default ProxiedPlayer getBungeeProxiedPlayer() {
        return ProxyServer.getInstance().getPlayer(getUniqueId());
    }

    @Override
    BungeeOnlinePlayer getOnlineInstance();


}
