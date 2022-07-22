package fr.pandacube.lib.players.standalone;

import java.util.Locale;
import java.util.UUID;

import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;

import fr.pandacube.lib.chat.ChatStatic;

public interface StandaloneOnlinePlayer extends StandaloneOffPlayer {
	
	
	
	
	/*
	 * General data and state
	 */
	
	/**
	 * @return The current name of this player
	 * @apiNote The implementation is expected to call the environment API
	 * (Bukkit/Bungee) to get the name of the player.
	 */
	
	String getServerName();
	
	String getWorldName();
	
	


	
	

	
	
	

	
	
	/*
	 * Sending packet and stuff to player
	 */
	
	/**
	 * Display the provided message in the player’s chat, if
	 * the chat is activated.
	 * @param message the message to display.
	 */
	void sendMessage(Component message);
	
	/**
	 * Display the provided message in the player’s chat, if
	 * the chat is activated.
	 * @param message the message to display.
	 */
	default void sendMessage(ComponentLike message) {
		sendMessage(message.asComponent());
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
	void sendMessage(Component message, Identified sender);

	/**
	 * Display the provided message in the player’s chat, if
	 * they allows to display CHAT messages
	 * @param message the message to display
	 * @param sender the player causing the send of this message. Client side filtering may occur.
	 * May be null if we don’t want client filtering, but still consider the message as CHAT message.
	 * @implNote implementation of this method should not filter the send of the message, based on
	 * the sender. This parameter is only there to be transmitted to the client, so client side filtering can
	 * be processed.
	 */
	default void sendMessage(ComponentLike message, UUID sender) {
		sendMessage(message.asComponent(), () -> sender == null ? Identity.nil() : Identity.identity(sender));
	}
	
	/**
	 * Display the provided message in the player’s chat, if the chat is
	 * activated, prepended with the server prefix.
	 * @param message the message to display
	 */
	default void sendPrefixedMessage(ComponentLike message) {
		sendMessage(ChatStatic.prefixedAndColored(message));
	}
	
	/**
	 * Display a title in the middle of the screen.
	 * @param title The big text
	 * @param subtitle The less big text
	 * @param fadeIn Fade in time in tick 
	 * @param stay Stay time in tick
	 * @param fadeOut Fade out time in tick
	 */
	void sendTitle(Component title, Component subtitle, int fadeIn, int stay, int fadeOut);
	
	/**
	 * Display a title in the middle of the screen.
	 * @param title The big text
	 * @param subtitle The less big text
	 * @param fadeIn Fade in time in tick 
	 * @param stay Stay time in tick
	 * @param fadeOut Fade out time in tick
	 */
    default void sendTitle(ComponentLike title, ComponentLike subtitle, int fadeIn, int stay, int fadeOut) {
    	sendTitle(title.asComponent(), subtitle.asComponent(), fadeIn, stay, fadeOut);
    }
	
    /**
     * Update the server brand field in the debug menu (F3) of the player
     * (third line in 1.15 debug screen). Supports ChatColor codes but no
     * line break.
     * @param brand the server brand to send to the client.
     */
	void sendServerBrand(String brand);
	
	
	
	
	
	
	
	
	/*
	 * Client options
	 */
	

	
	ClientOptions getClientOptions();
	
	interface ClientOptions {
		
		Locale getLocale();
		
		int getViewDistance();
		
		
		
		boolean hasChatColorEnabled();
		
		/**
		 * Tells if the client is configured to completely hide the chat to the
		 * player. When this is the case, nothing is displayed in the chat box,
		 * and the player can’t send any message or command.
		 * @implSpec if the value is unknown, it is assumed that the chat is
		 * fully visible.
		 */
		boolean isChatHidden();

		/**
		 * Tells if the client is configured to display the chat normally.
		 * When this is the case, chat messages and system messages are
		 * displayed in the chat box, and the player can send messages and
		 * commands.
		 * @implSpec if the value is unknown, it is assumed that the chat is
		 * fully visible.
		 */
		boolean isChatFullyVisible();

		/**
		 * Tells if the client is configured to only display system messages
		 * in the chat.
		 * When this is the case, chat messages are hidden but system messages
		 * are visible in the chat box, and the player can only send commands.
		 * @implSpec if the value is unknown, it is assumed that the chat is
		 * fully visible.
		 */
		boolean isChatOnlyDisplayingSystemMessages();
		
		
		
		/**
		 * Tells if the client has configured the main hand on the left.
		 * @implSpec if the value is unknown, it is assumed that the main hand
		 * is on the right.
		 */
		boolean isLeftHanded();

		/**
		 * Tells if the client has configured the main hand on the right.
		 * @implSpec if the value is unknown, it is assumed that the main hand
		 * is on the right.
		 */
		boolean isRightHanded();
		
		
		/**
		 * Tells if the client has enabled the filtering of texts on sign and book titles.
		 * Always false as of MC 1.18.
		 */
		boolean isTextFilteringEnabled();
		
		
		/**
		 * Tells if the client allows the server to list their player name in the
		 * multiplayer menu.
		 */
		boolean allowsServerListing();
		
		
		
		boolean hasSkinCapeEnabled();
		
		boolean hasSkinJacketEnabled();
		
		boolean hasSkinLeftSleeveEnabled();
		
		boolean hasSkinRightSleeveEnabled();
		
		boolean hasSkinLeftPantsEnabled();
		
		boolean hasSkinRightPantsEnabled();
		
		boolean hasSkinHatsEnabled();
		
	}
	
	/**
	 * Tells if the player can send chat messages or receive chat messages from
	 * other players, according to their client configuration.
	 * <br>
	 * Chat messages represent public communication between players. By default,
	 * it only include actual chat message. This method may be used in commands
	 * like /me, /afk or the login/logout broadcasted messages
	 */
	default boolean canChat() {
		return getClientOptions().isChatFullyVisible();
	}
	

}
