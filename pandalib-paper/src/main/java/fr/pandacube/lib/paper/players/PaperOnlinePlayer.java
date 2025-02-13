package fr.pandacube.lib.paper.players;

import com.destroystokyo.paper.ClientOption;
import com.destroystokyo.paper.ClientOption.ChatVisibility;
import com.destroystokyo.paper.SkinParts;
import fr.pandacube.lib.paper.players.PlayerNonPersistentConfig.ExpirationPolicy;
import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftPlayer;
import fr.pandacube.lib.players.standalone.AbstractOnlinePlayer;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import io.papermc.paper.entity.TeleportFlag.Relative;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.PlayerInventory;

import java.util.Locale;

/**
 * Represents any online player on a paper server.
 */
public interface PaperOnlinePlayer extends PaperOffPlayer, AbstractOnlinePlayer {

    /**
     * The default value for {@link Player#getWalkSpeed()}.
     */
    float BUKKIT_DEFAULT_WALK_SPEED = 0.2f;

    /**
     * The default value for {@link Player#getFlySpeed()}.
     */
    float BUKKIT_DEFAULT_FLY_SPEED = 0.1f;


    /*
     * General data and state
     */

    @Override
    default String getName() {
        return getBukkitPlayer().getName();
    }

    @Override
    default String getWorldName() {
        return getBukkitPlayer().getWorld().getName();
    }




    /*
     * Related class instances
     */

    @Override
    default OfflinePlayer getBukkitOfflinePlayer() {
        return getBukkitPlayer();
    }

    /**
     * Returns the OBC.CraftPlayer instance wrapped into the {@link CraftPlayer} reflection wrapper.
     * @return the OBC.CraftPlayer instance wrapped into the {@link CraftPlayer} reflection wrapper.
     */
    default CraftPlayer getWrappedCraftPlayer() {
        return ReflectWrapper.wrapTyped(getBukkitPlayer(), CraftPlayer.class);
    }





    /*
     * Permissions and groups
     */

    /**
     * Tells if this online player has the specified permission.
     */
    default boolean hasPermission(String permission) {
        return getBukkitPlayer().hasPermission(permission);
    }





    /*
     * Sending packet and stuff to player
     */

    @Override
    default void sendMessage(Component message) {
        getBukkitPlayer().sendMessage(message);
    }

    @Override
    default void sendTitle(Component title, Component subtitle, int fadeIn, int stay, int fadeOut) {
        getBukkitPlayer().showTitle(Title.title(title, subtitle, Times.times(Ticks.duration(fadeIn), Ticks.duration(stay), Ticks.duration(fadeOut))));
    }

    /**
     * Play a sound on this player’s client, sourced at this player’s location.
     * @param sound the sound to play
     * @param volume the volume of the sound.
     * @param pitch the pitch in which the sound is played.
     */
    default void playSound(Sound sound, float volume, float pitch) {
        playSound(sound, getBukkitPlayer().getLocation(), volume, pitch);
    }

    /**
     * Play a sound on this player’s client.
     * @param sound the sound to play
     * @param location the source location of the sound.
     * @param volume the volume of the sound.
     * @param pitch the pitch in which the sound is played.
     */
    default void playSound(Sound sound, Location location, float volume, float pitch) {
        getBukkitPlayer().playSound(location, sound, volume, pitch);
    }





    /*
     * Client options
     */

    @Override
    default PaperClientOptions getClientOptions() {
        return new PaperClientOptions(this);
    }

    /**
     * Provides various configuration values of the Minecraft client.
     */
    class PaperClientOptions implements AbstractOnlinePlayer.ClientOptions {

        private final PaperOnlinePlayer op;

        /**
         * Create a new instance of {@link PaperClientOptions}.
         * @param op the {@link PaperOnlinePlayer} instance.
         */
        protected PaperClientOptions(PaperOnlinePlayer op) {
            this.op = op;
        }

        @Override
        public boolean hasChatColorEnabled() {
            return op.getBukkitPlayer().getClientOption(ClientOption.CHAT_COLORS_ENABLED);
        }

        /**
         * Gets the chat visibility configuration.
         * @return the chat visibility configuration.
         */
        public ChatVisibility getChatVisibility() {
            return op.getBukkitPlayer().getClientOption(ClientOption.CHAT_VISIBILITY);
        }

        @Override
        public boolean isChatFullyVisible() {
            ChatVisibility v = getChatVisibility();
            return v == ChatVisibility.FULL;
        }

        @Override
        public boolean isChatOnlyDisplayingSystemMessages() {
            return getChatVisibility() == ChatVisibility.SYSTEM;
        }

        @Override
        public boolean isChatHidden() {
            return getChatVisibility() == ChatVisibility.HIDDEN;
        }

        @Override
        public Locale getLocale() {
            return op.getBukkitPlayer().locale();
        }

        @Override
        public int getViewDistance() {
            return op.getBukkitPlayer().getClientViewDistance();
        }

        /**
         * Gets the player’s main hand.
         * @return the player’s main hand.
         */
        public MainHand getMainHand() {
            return op.getBukkitPlayer().getMainHand();
        }

        @Override
        public boolean isLeftHanded() {
            return getMainHand() == MainHand.LEFT;
        }

        @Override
        public boolean isRightHanded() {
            return getMainHand() == MainHand.RIGHT;
        }

        @Override
        public boolean isTextFilteringEnabled() {
            return op.getBukkitPlayer().getClientOption(ClientOption.TEXT_FILTERING_ENABLED);
        }

        @Override
        public boolean allowsServerListing() {
            return op.getBukkitPlayer().isAllowingServerListings();
        }

        /**
         * Gets the player’s skin configuration.
         * @return the player’s skin configuration.
         */
        public SkinParts getSkinParts() {
            return op.getBukkitPlayer().getClientOption(ClientOption.SKIN_PARTS);
        }

        @Override
        public boolean hasSkinCapeEnabled() {
            return getSkinParts().hasCapeEnabled();
        }

        @Override
        public boolean hasSkinJacketEnabled() {
            return getSkinParts().hasJacketEnabled();
        }

        @Override
        public boolean hasSkinLeftSleeveEnabled() {
            return getSkinParts().hasLeftSleeveEnabled();
        }

        @Override
        public boolean hasSkinRightSleeveEnabled() {
            return getSkinParts().hasRightSleeveEnabled();
        }

        @Override
        public boolean hasSkinLeftPantsEnabled() {
            return getSkinParts().hasLeftPantsEnabled();
        }

        @Override
        public boolean hasSkinRightPantsEnabled() {
            return getSkinParts().hasRightPantsEnabled();
        }

        @Override
        public boolean hasSkinHatsEnabled() {
            return getSkinParts().hasHatsEnabled();
        }
    }








    /*
     * Relative teleport
     */

    /**
     * Teleports this player to the specified relative location, using the {@link Relative} flags.
     * @param relX the relative x coordinate.
     * @param relY the relative y coordinate.
     * @param relZ the relative z coordinate.
     */
    default void teleportRelatively(float relX, float relY, float relZ) {
        getBukkitPlayer().teleport(getBukkitPlayer().getLocation().add(relX, relY, relZ), Relative.VELOCITY_X, Relative.VELOCITY_Y, Relative.VELOCITY_Z, Relative.VELOCITY_ROTATION);
    }

    /**
     * Teleports this player to the specified location, using the {@link Relative} flags.
     * @param destination the destination.
     */
    default void teleportRelatively(Location destination) {
        getBukkitPlayer().teleport(destination, Relative.VELOCITY_X, Relative.VELOCITY_Y, Relative.VELOCITY_Z, Relative.VELOCITY_ROTATION);
    }







    /*
     * Player config
     */

    /**
     * Gets the non-persistent value of the provided configuration key of this player.
     * @param key the configuration key.
     * @return the value of the configuration, or null if the configuration is not set.
     */
    default String getNonPersistentConfig(String key) {
        return PlayerNonPersistentConfig.getData(getUniqueId(), key);
    }

    /**
     * Gets the non-persistent value of the provided configuration key of this player.
     * @param key the configuration key.
     * @param deflt the default value if the configuration is not set.
     * @return the value of the configuration, or {@code deflt} if the configuration is not set.
     */
    default String getNonPersistentConfig(String key, String deflt) {
        return PlayerNonPersistentConfig.getData(getUniqueId(), key);
    }

    /**
     * Sets the non-persistent value of the provided configuration key for this player.
     * @param key the configuration key to set.
     * @param value the new value.
     * @param expirationPolicy the expiration policy.
     */
    default void setNonPersistentConfig(String key, String value, ExpirationPolicy expirationPolicy) {
        PlayerNonPersistentConfig.setData(getUniqueId(), key, value, expirationPolicy);
    }

    /**
     * Unsets the non-persistent value of the provided configuration key for this player.
     * @param key the configuration key to update.
     */
    default void unsetNonPersistentConfig(String key) {
        PlayerNonPersistentConfig.unsetData(getUniqueId(), key);
    }




    /*
     * Player data
     */

    @Override
    default PlayerInventory getInventory() {
        return getBukkitPlayer().getInventory();
    }

    @Override
    default Inventory getEnderChest() {
        return getBukkitPlayer().getEnderChest();
    }


}
