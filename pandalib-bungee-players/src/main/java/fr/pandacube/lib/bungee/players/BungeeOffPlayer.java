package fr.pandacube.lib.bungee.players;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import fr.pandacube.lib.players.standalone.StandaloneOffPlayer;

public interface BungeeOffPlayer extends StandaloneOffPlayer {

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
