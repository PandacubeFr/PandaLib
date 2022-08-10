package fr.pandacube.lib.bungee.players;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import fr.pandacube.lib.players.standalone.AbstractOffPlayer;

public interface BungeeOffPlayer extends AbstractOffPlayer {

    /*
     * Related class instances
     */

    /**
     * @return l'instance Bungee du joueur en ligne, ou null si il n'est pas en
     *         ligne
     */
    default ProxiedPlayer getBungeeProxiedPlayer() {
        return ProxyServer.getInstance().getPlayer(getUniqueId());
    }

    @Override
    BungeeOnlinePlayer getOnlineInstance();


}
