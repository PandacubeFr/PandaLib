package fr.pandacub.lib.paper.players;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import fr.pandacube.lib.players.standalone.AbstractOffPlayer;

/**
 * Represents any player on a paper server, either offline or online.
 */
public interface PaperOffPlayer extends AbstractOffPlayer {

    /*
     * General data and state
     */

    @Override
    default boolean isOnline() {
        return getBukkitPlayer() != null;
    }




    /*
     * Related class instances
     */

    @Override
    PaperOnlinePlayer getOnlineInstance();

    /**
     * Returns the Bukkit online {@link Player} instance of this player, or null if not available (offline).
     * @return the Bukkit online {@link Player} instance of this player, or null if not available (offline).
     */
    default Player getBukkitPlayer() {
        return Bukkit.getPlayer(getUniqueId());
    }

    /**
     * Get the Bukkit’s {@link OfflinePlayer} instance for this player.
     * May represent a player that never joined the server before.
     * @return an instance of {@link OfflinePlayer} for this player.
     */
    default OfflinePlayer getBukkitOfflinePlayer() {
        return Bukkit.getOfflinePlayer(getUniqueId());
    }





    /*
     * Display name
     */
    /**
     * Get the display name of the user, in legacy format.
     * @return the display name of the player.
     *
     * @implNote This default implementation gets the display name from bukkit (if the player is online).
     * If its different to the player name, it returns it. Otherwise, it tries to generate the team displayname with {@link #getTeamDisplayName()}.
     * If the player is not in a team, then the player name is used.
     */
    @Override
    default String getDisplayName() {
        String name = getName();
        Player p = getBukkitPlayer();
        @SuppressWarnings("deprecation")
        String bukkitDispName = p != null ? p.getDisplayName() : name;
        if (!name.equals(bukkitDispName))
            return bukkitDispName;
        String teamDispName = getTeamDisplayName();
        return teamDispName == null ? name : teamDispName;
    }

    /**
     * Computes and returns the the name of the player with the prefix, suffix and color of the team the player is in.
     * @return The legacy formated player display name, if he is in a {@link Team}, or null otherwise.
     */
    default String getTeamDisplayName() {
        String name = getName();
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(name);
        if (team == null)
            return null;
        @SuppressWarnings("deprecation")
        String teamPrefix = team.getPrefix();
        @SuppressWarnings("deprecation")
        String teamSuffix = team.getSuffix();
        @SuppressWarnings("deprecation")
        String teamColor = team.getColor().toString();

        return teamPrefix + teamColor + name + teamSuffix;
    }

}
