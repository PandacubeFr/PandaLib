package fr.pandacube.lib.paper.players;

import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftItemStack;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftServer;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CompoundTag;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.ListTag;
import fr.pandacube.lib.players.standalone.AbstractOffPlayer;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

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

    default CompoundTag getPlayerData() {
        return ReflectWrapper.wrapTyped(Bukkit.getServer(), CraftServer.class)
                .getServer()
                .getPlayerList()
                .playerIo()
                .getPlayerData(getUniqueId().toString());
    }

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