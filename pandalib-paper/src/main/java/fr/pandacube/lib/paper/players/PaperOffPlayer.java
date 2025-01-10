package fr.pandacube.lib.paper.players;

import fr.pandacube.lib.paper.world.PrimaryWorlds;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftServer;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CompoundTag;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.NbtIo;
import fr.pandacube.lib.paper.players.PlayerDataWrapper.PlayerDataLoadException;
import fr.pandacube.lib.paper.world.WorldUtil;
import fr.pandacube.lib.players.standalone.AbstractOffPlayer;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.UnaryOperator;

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
     * If it's different to the player name, it returns it. Otherwise, it tries to generate the team display name with {@link #getTeamDisplayName()}.
     * If the player is not in a team, then the player name is used.
     */
    @Override
    default String getDisplayName() {
        String name = getName();
        Player p = getBukkitPlayer();
        @SuppressWarnings("deprecation")
        String bukkitDisplayName = p != null ? p.getDisplayName() : name;
        if (!name.equals(bukkitDisplayName))
            return bukkitDisplayName;
        String teamDisplayName = getTeamDisplayName();
        return teamDisplayName == null ? name : teamDisplayName;
    }

    /**
     * Computes and returns the name of the player with the prefix, suffix and color of the team the player is in.
     * @return The legacy formatted player display name, if he is in a {@link Team}, or null otherwise.
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





    /*
     * Player config
     */

    @SuppressWarnings("RedundantThrows") // may be thrown by concrete implementation
    @Override
    default String getConfig(String key) throws Exception {
        return PaperPlayerConfigStorage.get(getUniqueId(), key);
    }

    @SuppressWarnings("RedundantThrows") // may be thrown by concrete implementation
    @Override
    default String getConfig(String key, String deflt) throws Exception {
        return PaperPlayerConfigStorage.get(getUniqueId(), key, deflt);
    }

    @SuppressWarnings("RedundantThrows") // may be thrown by concrete implementation
    @Override
    default void setConfig(String key, String value) throws Exception {
        PaperPlayerConfigStorage.set(getUniqueId(), key, value);
    }

    @SuppressWarnings("RedundantThrows") // may be thrown by concrete implementation
    @Override
    default void updateConfig(String key, String deflt, UnaryOperator<String> updater) throws Exception {
        PaperPlayerConfigStorage.update(getUniqueId(), key, deflt, updater);
    }

    @SuppressWarnings("RedundantThrows") // may be thrown by concrete implementation
    @Override
    default void unsetConfig(String key) throws Exception {
        PaperPlayerConfigStorage.unset(getUniqueId(), key);
    }




    /*
     * Player data
     */

    /**
     * Gets the NBT data from the player-data file.
     * It will not work if the player is online, because the data on the file are not synchronized with real-time values.
     * @return the NBT data from the player-data file, or null if the file does not exist.
     * @throws IllegalStateException if the player is online.
     */
    default CompoundTag getPlayerData() {
        if (isOnline())
            throw new IllegalStateException("Cannot access data file of " + getName() + " because they're online.");
        try {
            return ReflectWrapper.wrapTyped(Bukkit.getServer(), CraftServer.class)
                    .getServer()
                    .getPlayerList()
                    .playerIo()
                    .load(getName(), getUniqueId().toString()).orElse(null);
        } catch (Exception|LinkageError e) {
            throw new PlayerDataLoadException(getName(), getUniqueId(), e);
        }
    }

    /**
     * Gets a wrapper for the NBT data from the player-data file.
     * It will not work if the player is online, because the data on the file are not synchronized with real-time values.
     * @return the NBT data from the player-data file.
     * @throws IllegalStateException if the player is online.
     */
    default PlayerDataWrapper getPlayerDataWrapper() {
        return new PlayerDataWrapper(getPlayerData());
    }

    /**
     * Saves the provided NBT data to the player-data file.
     * It will not work if the player is online, because the provided data will be lost when the player disconnects.
     * @param data the data to save.
     * @throws IllegalStateException if the player is online.
     * @throws IOException if an IO error occurs.
     */
    default void savePlayerData(PlayerDataWrapper data) throws IOException {
        if (isOnline())
            throw new IllegalStateException("Cannot write data file of " + getName() + " because they’re online.");
        File file = getPlayerDataFile(false);
        File old = getPlayerDataFile(true);
        old.delete();
        Files.move(file.toPath(), old.toPath());
        NbtIo.writeCompressed(data.data(), file.toPath());
    }

    /**
     * Gets the file where the player-data is stored.
     * @param old true to return the path of old data, false to return the actual file.
     * @return the file where the player-data is stored.
     */
    default File getPlayerDataFile(boolean old) {
        File playerDataDir = new File(WorldUtil.worldDir(PrimaryWorlds.PRIMARY_WORLDS.getFirst()), "playerdata");
        return new File(playerDataDir, getUniqueId() + (old ? ".dat_old" : ".dat"));
    }

    /**
     * Gets the player’s inventory.
     * @return the player’s inventory.
     */
    default PlayerInventory getInventory() {
        return getPlayerDataWrapper().getInventory();
    }

    /**
     * Gets the player’s enderchest.
     * @return the player’s enderchest.
     */
    default Inventory getEnderChest() {
        return getPlayerDataWrapper().getEnderChest();
    }

}
