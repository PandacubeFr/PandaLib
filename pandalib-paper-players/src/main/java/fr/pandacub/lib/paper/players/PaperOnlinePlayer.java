package fr.pandacub.lib.paper.players;

import java.util.Locale;
import java.util.UUID;

import com.destroystokyo.paper.ClientOption;
import com.destroystokyo.paper.ClientOption.ChatVisibility;
import com.destroystokyo.paper.SkinParts;
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
import org.bukkit.entity.Player;
import org.bukkit.inventory.MainHand;

import fr.pandacube.lib.players.standalone.StandaloneOnlinePlayer;

public interface PaperOnlinePlayer extends PaperOffPlayer, StandaloneOnlinePlayer {

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

    /**
     * @return l'instance Bukkit du joueur en ligne, ou null si il n'est pas en
     *         ligne
     */
    Player getBukkitPlayer();

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

    default void playSound(Sound sound, float volume, float pitch) {
        playSound(sound, getBukkitPlayer().getLocation(), volume, pitch);
    }

    default void playSound(Sound sound, Location location, float volume, float pitch) {
        getBukkitPlayer().playSound(location, sound, volume, pitch);
    }





    /*
     * Client options
     */

    @Override
    PaperClientOptions getClientOptions();

    abstract class PaperClientOptions implements StandaloneOnlinePlayer.ClientOptions {

        private final PaperOnlinePlayer op;

        public PaperClientOptions(PaperOnlinePlayer op) {
            this.op = op;
        }

        @Override
        public boolean hasChatColorEnabled() {
            return op.getBukkitPlayer().getClientOption(ClientOption.CHAT_COLORS_ENABLED);
        }

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
        public abstract boolean isTextFilteringEnabled(); // needs reflection

        @Override
        public boolean allowsServerListing() {
            return op.getBukkitPlayer().isAllowingServerListings();
        }

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

    default void damage(double amount) {
        getBukkitPlayer().damage(amount); // uses DamageSource.GENERIC
    }

    default void damage(double amount, LivingEntity source) {
        getBukkitPlayer().damage(amount, source); // uses appropriate DamageSource according to provided player or entity
    }


}
