package fr.pandacube.lib.core.players;

import java.util.Locale;
import java.util.UUID;

import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import fr.pandacube.lib.core.chat.Chat;
import fr.pandacube.lib.core.db.DBException;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;

public interface IOnlinePlayer extends IOffPlayer {
	
	
	
	
	/*
	 * General data and state
	 */
	
	/**
	 * @return The current name of this player
	 * @implSpec The implementation is expected to call the environment API
	 * (Bukkit/Bungee) to get the name of the player.
	 */
	public abstract String getName();
	
	public abstract String getServerName();
	
	public abstract String getWorldName();
	
	
	
	
	/*
	 * Floodgate related
	 */
	
	public default boolean isBedrockClient() {
		return FloodgateApi.getInstance().isFloodgatePlayer(getUniqueId());
	}
	
	public default FloodgatePlayer getBedrockClient() {
		return FloodgateApi.getInstance().getPlayer(getUniqueId());
	}
	

	
	
	/*
	 * Related class instances
	 */

	/**
	 * @throws IllegalStateException if the player was not found in the database (should never happen)
	 * @throws DBException if a database access error occurs
	 */
	@Override
	public default SQLPlayer getDbPlayer() throws DBException {
		SQLPlayer p = SQLPlayer.getPlayerFromUUID(getUniqueId());
		if (p == null)
			throw new IllegalStateException("The player was not found in the database: " + getUniqueId());
		return p;
	}
	
	
	
	
	
	
	
	/*
	 * Permissions and groups
	 */

	/**
	 * Tells if this online player has the specified permission.
	 * @implSpec the implementation of this method must not directly or
	 * indirectly call the method {@link IOffPlayer#hasPermission(String)},
	 * or it may result in a {@link StackOverflowError}.
	 */
	public abstract boolean hasPermission(String permission);

	/**
	 * Tells if this online player has the permission resulted from the provided expression.
	 * @implSpec the implementation of this method must not directly or
	 * indirectly call the method {@link IOffPlayer#hasPermissionExpression(String)},
	 * or it may result in a {@link StackOverflowError}.
	 */
	public abstract boolean hasPermissionExpression(String permission);
	
	
	
	
	
	
	
	
	
	/*
	 * Vanish
	 */
	
	public abstract boolean isVanished();
	
	public default boolean isVanishedFor(IOffPlayer other) {
		if (!isVanished())
			return false; // can see unvanished
		
		if (getUniqueId().equals(other.getUniqueId()))
			return false; // can see themself
		
		if (!isInStaff() && other.isInStaff())
			return false; // can see non-staff as a staff
		
		if (other.hasPermission("pandacube.vanish.see." + getUniqueId()))
			return false; // can see if has a specific permission
		
		return true;
	}
	
	
	
	
	
	
	
	/*
	 * Sending packet and stuff to player
	 */
	
	/**
	 * Display the provided message in the player’s chat, if
	 * the chat is activated.
	 * @param message the message to display.
	 */
	public abstract void sendMessage(Component message);
	
	/**
	 * Display the provided message in the player’s chat, if
	 * the chat is activated.
	 * @param message the message to display.
	 */
	public default void sendMessage(ComponentLike message) {
		sendMessage(message.asComponent());
	}

	/**
	 * Display the provided message in the player’s chat, if
	 * the chat is activated
	 * @param message the message to display
	 */
	public default void sendMessage(Chat message) {
		sendMessage(message.getAdv());
	}

	/**
	 * Display the provided message in the player’s chat, if
	 * they allows to display CHAT messages
	 * @param message the message to display.
	 * @param sender the player causing the send of this message. Client side filtering may occur.
	 * May be null if we don’t want client filtering, but still consider the message as CHAT message.
	 * @implNote implementation of this method should not filter the send of the message, based on
	 * the sender. This parameter is only there to be transmitted to the client, so client side filtering can
	 * be processed.
	 */
	public default void sendMessage(Component message, UUID sender) {
		sendMessage(message, () -> sender == null ? Identity.nil() : Identity.identity(sender));
	}

	/**
	 * Display the provided message in the player’s chat, if
	 * they allows to display CHAT messages
	 * @param message the message to display.
	 * @param sender the player causing the send of this message. Client side filtering may occur.
	 * May be null if we don’t want client filtering, but still consider the message as CHAT message.
	 * @implNote implementation of this method should not filter the send of the message, based on
	 * the sender. This parameter is only there to be transmitted to the client, so client side filtering can
	 * be processed.
	 */
	public abstract void sendMessage(Component message, Identified sender);

	/**
	 * Display the provided message in the player’s chat, if
	 * they allows to display CHAT messages
	 * @param message the message to display
	 * @param sender the player causing the send of this message. Client side filtering may occur.
	 * May be null if we don’t want client filtering, but still consider the message as CHAT message.
	 */
	public default void sendMessage(ComponentLike message, UUID sender) {
		sendMessage(message.asComponent(), sender);
	}

	/**
	 * Display the provided message in the player’s chat, if
	 * they allows to display CHAT messages
	 * @param message the message to display
	 * @param sender the player causing the send of this message. Client side filtering may occur.
	 * May be null if we don’t want client filtering, but still consider the message as CHAT message.
	 */
	public default void sendMessage(Chat message, UUID sender) {
		sendMessage(message.getAdv(), sender);
	}
	
	/**
	 * Display the provided message in the player’s chat, if the chat is
	 * activated, prepended with the server prefix.
	 * @param message the message to display
	 */
	public default void sendPrefixedMessage(Component message) {
		sendMessage(IPlayerManager.prefixedAndColored(message));
	}
	
	/**
	 * Display the provided message in the player’s chat, if the chat is
	 * activated, prepended with the server prefix.
	 * @param message the message to display
	 */
	public default void sendPrefixedMessage(Chat message) {
		sendPrefixedMessage(message.getAdv());
	}
	
	/**
	 * Display a title in the middle of the screen.
	 * @param title The big text
	 * @param subtitle The less big text
	 * @param fadeIn Fade in time in tick 
	 * @param stay Stay time in tick
	 * @param fadeOut Fade out time in tick
	 */
    public abstract void sendTitle(Component title, Component subtitle, int fadeIn, int stay, int fadeOut);
	
	/**
	 * Display a title in the middle of the screen.
	 * @param title The big text
	 * @param subtitle The less big text
	 * @param fadeIn Fade in time in tick 
	 * @param stay Stay time in tick
	 * @param fadeOut Fade out time in tick
	 */
    public default void sendTitle(Chat title, Chat subtitle, int fadeIn, int stay, int fadeOut) {
    	sendTitle(title.getAdv(), subtitle.getAdv(), fadeIn, stay, fadeOut);
    }
	
    /**
     * Update the server brand field in the debug menu (F3) of the player
     * (third line in 1.15 debug screen). Supports ChatColor codes but no
     * line break.
     * @param brand the server brand to send to the client.
     */
	public abstract void sendServerBrand(String brand);
	
	
	
	
	
	
	
	
	/*
	 * Client options
	 */
	

	
	public abstract ClientOptions getClientOptions();
	
	public interface ClientOptions {
		
		public Locale getLocale();
		
		public int getViewDistance();
		
		
		
		public boolean hasChatColorEnabled();
		
		/**
		 * Tells if the client is configured to completely hide the chat to the
		 * player. When this is the case, nothing is displayed in the chat box,
		 * and the player can’t send any message or command.
		 * @implSpec if the value is unknown, it is assumed that the chat is
		 * fully visible.
		 */
		public boolean isChatHidden();

		/**
		 * Tells if the client is configured to display the chat normally.
		 * When this is the case, chat messages and system messages are
		 * displayed in the chat box, and the player can send messages and
		 * commands.
		 * @implSpec if the value is unknown, it is assumed that the chat is
		 * fully visible.
		 */
		public boolean isChatFullyVisible();

		/**
		 * Tells if the client is configured to only display system messages
		 * in the chat.
		 * When this is the case, chat messages are hidden but system messages
		 * are visible in the chat box, and the player can only send commands.
		 * @implSpec if the value is unknown, it is assumed that the chat is
		 * fully visible.
		 */
		public boolean isChatOnlyDisplayingSystemMessages();
		
		
		
		/**
		 * Tells if the client has configured the main hand on the left.
		 * @implSpec if the value is unknown, it is assumed that the main hand
		 * is on the right.
		 */
		public boolean isLeftHanded();

		/**
		 * Tells if the client has configured the main hand on the right.
		 * @implSpec if the value is unknown, it is assumed that the main hand
		 * is on the right.
		 */
		public boolean isRightHanded();
		
		
		
		public boolean hasSkinCapeEnabled();
		
		public boolean hasSkinJacketEnabled();
		
		public boolean hasSkinLeftSleeveEnabled();
		
		public boolean hasSkinRightSleeveEnabled();
		
		public boolean hasSkinLeftPantsEnabled();
		
		public boolean hasSkinRightPantsEnabled();
		
		public boolean hasSkinHatsEnabled();
		
	}
	
	/**
	 * Tells if the player can send chat messages or receive chat messages from
	 * other players, according to their client configuration.
	 * <br>
	 * Chat messages represent public communication between players. By default,
	 * it only include actual chat message. This method may be used in commands
	 * like /me, /afk or the login/logout broadcasted messages
	 * @return
	 */
	public default boolean canChat() {
		return getClientOptions().isChatFullyVisible();
	}
	

}
