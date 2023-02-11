package fr.pandacube.lib.bungee.players;

import java.util.Locale;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.SkinConfiguration;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer.ChatMode;
import net.md_5.bungee.api.connection.ProxiedPlayer.MainHand;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.ClientSettings;
import net.md_5.bungee.protocol.packet.PluginMessage;

import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.players.standalone.AbstractOnlinePlayer;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.util.MinecraftVersion;

/**
 * Represents any online player on a Bungeecord proxy.
 */
public interface BungeeOnlinePlayer extends BungeeOffPlayer, AbstractOnlinePlayer {

    /*
     * General data and state
     */


    @Override
    default String getName() {
        return getBungeeProxiedPlayer().getName();
    }

    @Override
    default String getServerName() {
        if (getServer() == null) return null;
        return getServer().getInfo().getName();
    }

    /**
     * Returns the server on which the player is.
     * @return the server on which the player is.
     */
    default Server getServer() {
        return getBungeeProxiedPlayer().getServer();
    }

    /**
     * Gets the minecraft version of this player’s client.
     * @return the minecraft version of this player’s client.
     */
    default MinecraftVersion getMinecraftVersion() {
        return MinecraftVersion.getVersion(getBungeeProxiedPlayer().getPendingConnection().getVersion());
    }







    /*
     * Permissions and groups
     */

    /**
     * Tells if this online player has the specified permission.
     */
    default boolean hasPermission(String permission) {
        return getBungeeProxiedPlayer().hasPermission(permission);
    }






    /*
     * Sending packet and stuff to player
     */

    @Override
    default void sendMessage(Component message) {
        getBungeeProxiedPlayer().sendMessage(Chat.toBungee(message));
    }

    @Override
    default void sendMessage(ComponentLike message, UUID sender) {
        getBungeeProxiedPlayer().sendMessage(sender, Chat.toBungee(message.asComponent()));
    }

    @Override
    default void sendMessage(Component message, Identified sender) {
        getBungeeProxiedPlayer().sendMessage(sender == null ? null : sender.identity().uuid(), Chat.toBungee(message));
    }

    /**
     * Display the provided message in the player’s chat, if they allows to display CHAT messages.
     * @param message the message to send.
     * @param sender the player causing the send of this message. Client side filtering may occur. May be null if we
     *               don’t want client filtering, but still consider the message as CHAT message.
     */
    default void sendMessage(ComponentLike message, ProxiedPlayer sender) {
        getBungeeProxiedPlayer().sendMessage(sender == null ? null : sender.getUniqueId(), Chat.toBungee(message.asComponent()));
    }

    @Override
    default void sendTitle(Component title, Component subtitle, int fadeIn, int stay, int fadeOut) {
        ProxyServer.getInstance().createTitle()
                .title(Chat.toBungee(title)).subTitle(Chat.toBungee(subtitle))
                .fadeIn(fadeIn).stay(stay).fadeOut(fadeOut)
                .send(getBungeeProxiedPlayer());
    }

    @Override
    default void sendServerBrand(String legacyText) {
        try {
            ByteBuf payload = ByteBufAllocator.DEFAULT.heapBuffer();
            DefinedPacket.writeString(legacyText, payload);
            getBungeeProxiedPlayer().unsafe().sendPacket(new PluginMessage("minecraft:brand", DefinedPacket.toArray(payload), false));
            payload.release();
        } catch (Exception ignored) { }
    }






    /*
     * Client options
     */

    @Override
    default BungeeClientOptions getClientOptions() {
        return new BungeeClientOptions(this);
    }

    /**
     * Provides various configuration values of the Minecraft client.
     */
    class BungeeClientOptions implements AbstractOnlinePlayer.ClientOptions {

        private final BungeeOnlinePlayer op;

        /**
         * Create a new instance of {@link BungeeClientOptions}.
         * @param op the {@link BungeeOnlinePlayer} instance.
         */
        public BungeeClientOptions(BungeeOnlinePlayer op) {
            this.op = op;
        }

        private ClientSettings getBungeeSettings() {
            ProxiedPlayer pp = op.getBungeeProxiedPlayer();
            try {
                return (ClientSettings) Reflect.ofClassOfInstance(pp).method("getSettings").invoke(pp);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean hasChatColorEnabled() {
            return op.getBungeeProxiedPlayer().hasChatColors();
        }

        /**
         * Gets the chat visibility configuration.
         * @return the chat visibility configuration.
         */
        public ChatMode getChatMode() {
            return op.getBungeeProxiedPlayer().getChatMode();
        }

        @Override
        public boolean isChatFullyVisible() {
            ChatMode v = getChatMode();
            return v == ChatMode.SHOWN || v == null;
        }

        @Override
        public boolean isChatOnlyDisplayingSystemMessages() {
            return getChatMode() == ChatMode.COMMANDS_ONLY;
        }

        @Override
        public boolean isChatHidden() {
            return getChatMode() == ChatMode.HIDDEN;
        }

        @Override
        public Locale getLocale() {
            return op.getBungeeProxiedPlayer().getLocale();
        }

        @Override
        public int getViewDistance() {
            return op.getBungeeProxiedPlayer().getViewDistance();
        }

        /**
         * Gets the player’s main hand.
         * @return the player’s main hand.
         */
        public MainHand getMainHand() {
            return op.getBungeeProxiedPlayer().getMainHand();
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
            ClientSettings settings = getBungeeSettings();
            return settings != null && settings.isDisableTextFiltering(); // Bungee badly named the method
        }

        @Override
        public boolean allowsServerListing() {
            ClientSettings settings = getBungeeSettings();
            return settings != null && settings.isAllowServerListing();
        }

        /**
         * Gets the player’s skin configuration.
         * @return the player’s skin configuration.
         */
        public SkinConfiguration getSkinParts() {
            return op.getBungeeProxiedPlayer().getSkinParts();
        }

        @Override
        public boolean hasSkinCapeEnabled() {
            return getSkinParts().hasCape();
        }

        @Override
        public boolean hasSkinJacketEnabled() {
            return getSkinParts().hasJacket();
        }

        @Override
        public boolean hasSkinLeftSleeveEnabled() {
            return getSkinParts().hasLeftSleeve();
        }

        @Override
        public boolean hasSkinRightSleeveEnabled() {
            return getSkinParts().hasRightSleeve();
        }

        @Override
        public boolean hasSkinLeftPantsEnabled() {
            return getSkinParts().hasLeftPants();
        }

        @Override
        public boolean hasSkinRightPantsEnabled() {
            return getSkinParts().hasRightPants();
        }

        @Override
        public boolean hasSkinHatsEnabled() {
            return getSkinParts().hasHat();
        }
    }

}