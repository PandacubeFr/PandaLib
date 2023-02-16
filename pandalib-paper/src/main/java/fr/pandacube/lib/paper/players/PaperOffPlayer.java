package fr.pandacube.lib.paper.players;

import com.google.common.io.Files;
import fr.pandacube.lib.paper.reflect.util.PrimaryWorlds;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftItemStack;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftServer;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CompoundTag;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.ListTag;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.NbtIo;
import fr.pandacube.lib.paper.util.WorldUtil;
import fr.pandacube.lib.players.standalone.AbstractOffPlayer;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
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





    /*
     * Player config
     */

    @Override
    default String getConfig(String key) throws Exception {
        return PaperPlayerConfigStorage.get(getUniqueId(), key);
    }

    @Override
    default String getConfig(String key, String deflt) throws Exception {
        return PaperPlayerConfigStorage.get(getUniqueId(), key, deflt);
    }

    @Override
    default void setConfig(String key, String value) throws Exception {
        PaperPlayerConfigStorage.set(getUniqueId(), key, value);
    }

    @Override
    default void updateConfig(String key, String deflt, UnaryOperator<String> updater) throws Exception {
        PaperPlayerConfigStorage.update(getUniqueId(), key, deflt, updater);
    }

    @Override
    default void unsetConfig(String key) throws Exception {
        PaperPlayerConfigStorage.unset(getUniqueId(), key);
    }




    /*
     * Player data
     */

    /**
     * Gets the NBT data from the playerdata file.
     * It will not work if the player is online, because the data on the file are not synchronized with real-time values.
     * @return the NBT data from the playerdata file.
     * @throws IllegalStateException if the player is online.
     */
    default CompoundTag getPlayerData() {
        if (isOnline())
            throw new IllegalStateException("Cannot access data file of " + getName() + " because they’re online.");
        return ReflectWrapper.wrapTyped(Bukkit.getServer(), CraftServer.class)
                .getServer()
                .getPlayerList()
                .playerIo()
                .getPlayerData(getUniqueId().toString());
    }

    /**
     * Saves the provided NBT data to the playerdata file.
     * It will not work if the player is online, because the provided data will be lost when the player disconnects.
     * @param data the data to save.
     * @throws IllegalStateException if the player is online.
     * @throws IOException if an IO error occurs.
     */
    default void savePlayerData(CompoundTag data) throws IOException {
        if (isOnline())
            throw new IllegalStateException("Cannot write data file of " + getName() + " because they’re online.");
        File file = getPlayerDataFile(false);
        File old = getPlayerDataFile(true);
        old.delete();
        Files.move(file, old);
        NbtIo.writeCompressed(data, file);
    }

    /**
     * Gets the file where the playerdata is stored.
     * @param old true to return the path of old data, false to return the actual file.
     * @return the file where the playerdata is stored.
     */
    default File getPlayerDataFile(boolean old) {
        File playerDataDir = new File(WorldUtil.worldDir(PrimaryWorlds.PRIMARY_WORLDS.get(0)), "playerdata");
        return new File(playerDataDir, getUniqueId() + (old ? ".dat_old" : ".dat"));
    }

    /**
     * Gets the content of the player’s inventory.
     * @return the content of the player’s inventory.
     */
    default ItemStack[] getInventoryContent() {
        ItemStack[] content = new ItemStack[InventoryType.PLAYER.getDefaultSize()];
        CompoundTag playerData = getPlayerData();
        if (playerData == null)
            return content;
        ListTag nbttaglist = playerData.getList("Inventory", 10); // type of list element 10 is CompoundTag
        if (nbttaglist == null)
            return content;
        // cat   nbEl NBTslot   bukkitSlot NBT->Bukkit
        // items 36el           0-35       ==
        // armor  4el start 100 36-39      -100 + 36
        // offhnd 1el start 150 40         -150 + 40
        for (int i = 0; i < nbttaglist.size(); i++) {
            CompoundTag itemTag = nbttaglist.getCompound(i);
            ItemStack is = CraftItemStack.asCraftMirror(itemTag);
            if (is != null && !is.getType().isAir()) {
                int nbtSlot = itemTag.getByte("Slot") & 255;
                int bukkitSlot =  nbtSlot < 36              ? nbtSlot
                        : (nbtSlot >= 100 && nbtSlot < 104) ? nbtSlot - 100 + 36
                        :  nbtSlot == 150                   ? 40
                        : -1;
                if (bukkitSlot >= 0)
                    content[bukkitSlot] = is;
            }
        }

        return content;
    }

    /**
     * Gets the content of the player’s enderchest.
     * @return the content of the player’s enderchest.
     */
    default ItemStack[] getEnderchestContent() {
        ItemStack[] content = new ItemStack[InventoryType.ENDER_CHEST.getDefaultSize()];
        CompoundTag playerData = getPlayerData();
        if (playerData == null || !playerData.contains("EnderItems", 9)) // type 9 is list
            return content;
        ListTag nbtList = playerData.getList("EnderItems", 10); // type of list element 10 is CompoundTag
        if (nbtList == null)
            return content;
        for (int i = 0; i < nbtList.size(); i++) {
            CompoundTag itemTag = nbtList.getCompound(i);
            int nbtSlot = itemTag.getByte("Slot") & 255;
            ItemStack is = CraftItemStack.asCraftMirror(itemTag);
            if (nbtSlot < content.length && is != null && !is.getType().isAir()) {
                content[nbtSlot] = CraftItemStack.asCraftMirror(itemTag);
            }
        }

        return content;
    }

}
