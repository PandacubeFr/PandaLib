package fr.pandacube.lib.paper.players;

import com.destroystokyo.paper.ClientOption;
import com.destroystokyo.paper.ClientOption.ChatVisibility;
import com.destroystokyo.paper.SkinParts;
import fr.pandacube.lib.players.standalone.AbstractOnlinePlayer;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.MainHand;

import java.util.Locale;
import java.util.UUID;

/**
 * Represents any online player on a paper server.
 */
public interface PaperOnlinePlayer extends PaperOffPlayer, AbstractOnlinePlayer {

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
    default void sendMessage(ComponentLike message, UUID sender) {
        getBukkitPlayer().sendMessage(sender == null ? Identity.nil() : Identity.identity(sender), message, MessageType.CHAT);
    }

    @Override
    default void sendMessage(Component message, Identified sender) {
        getBukkitPlayer().sendMessage(sender == null ? Identity.nil() : sender.identity(), message, MessageType.CHAT);
    }

    @Override
    default void sendTitle(Component title, Component subtitle, int fadeIn, int stay, int fadeOut) {
        getBukkitPlayer().showTitle(Title.title(title, subtitle, Times.times(Ticks.duration(fadeIn), Ticks.duration(stay), Ticks.duration(fadeOut))));
    }

    /**
     * Play a sound on this player’s client, sourced at this player’s location.
     * @param sound the sound to play
     * @param volume the volume of the sound.
     * @param pitch the pich in which the sound is played.
     */
    default void playSound(Sound sound, float volume, float pitch) {
        playSound(sound, getBukkitPlayer().getLocation(), volume, pitch);
    }

    /**
     * Play a sound on this player’s client.
     * @param sound the sound to play
     * @param location the source location of the sound.
     * @param volume the volume of the sound.
     * @param pitch the pich in which the sound is played.
     */
    default void playSound(Sound sound, Location location, float volume, float pitch) {
        getBukkitPlayer().playSound(location, sound, volume, pitch);
    }





    /*
     * Client options
     */

    @Override
    PaperClientOptions getClientOptions();

    /**
     * Provides various configuration values of the Minecraft client.
     */
    abstract class PaperClientOptions implements AbstractOnlinePlayer.ClientOptions {

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
            return v == ChatVisibility.FULL || v == ChatVisibility.UNKNOWN;
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
        public boolean isTextFilteringEnabled() { // needs reflection to get the actual value
            return false;
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
     * Custom damage
     */

    /**
     * Deals damages to this player.
     * @param amount the amount of damage to deal.
     */
    default void damage(double amount) {
        getBukkitPlayer().damage(amount); // uses DamageSource.GENERIC
    }

    /**
     * Deals damages to this player, from the provided entity.
     * @param amount the amount of damage to deal.
     * @param source the entity from which the damage comes from.
     */
    default void damage(double amount, LivingEntity source) {
        getBukkitPlayer().damage(amount, source); // uses appropriate DamageSource according to provided player or entity
    }


}
