package fr.pandacube.lib.players.standalone;

import fr.pandacube.lib.chat.ChatStatic;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;

import java.util.Locale;
import java.util.UUID;

/**
 * Represents any online player.
 */
public interface AbstractOnlinePlayer extends AbstractOffPlayer {
	
	
	
	
	/*
	 * General data and state
	 */

	/**
	 * Returns the name of the current server the player is in.
	 * The returned value is used by the 'pandalib-permissions' system.
	 * @return the name of the current server the player is in.
	 */
	String getServerName();

	/**
	 * Returns the name of the current world the player is in.
	 * The returned value is used by the 'pandalib-permissions' system.
	 * @return the name of the current world the player is in.
	 */
	String getWorldName();







	/*
	 * Permissions and groups
	 */

	/**
	 * Tells if this online player has the specified permission.
	 * @param permission the permission to test on that player.
	 * @return weither this player has the specified permission or not.
	 * @implSpec Implementation of this method should call the permission system of their environment (paper/bungee),
	 * so this method will work independently of the usage of the 'pandalib-permissions' module.
	 */
	boolean hasPermission(String permission);
	

	

	
	

	
	
	/*
	 * Sending packet and stuff to player
	 */
	
	/**
	 * Display the provided message in the player’s chat, if the chat is activated.
	 * @param message the message to display.
	 */
	void sendMessage(Component message);
	
	/**
	 * Display the provided message in the player’s chat, if the chat is activated.
	 * @param message the message to display.
	 */
	default void sendMessage(ComponentLike message) {
		sendMessage(message.asComponent());
	}

	/**
	 * Display the provided message in the player’s chat, if they allows to display CHAT messages.
	 * @param message the message to display.
	 * @param sender the player causing the send of this message. Client side filtering may occur.
	 * May be null if we don’t want client filtering, but still consider the message as CHAT message.
	 * @implNote implementation of this method should not filter the send of the message, based on
	 * the sender. This parameter is only there to be transmitted to the client, so client side filtering can
	 * be processed.
	 */
	void sendMessage(Component message, Identified sender);

	/**
	 * Display the provided message in the player’s chat, if they allows to display CHAT messages.
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
	 * Display the provided message in the player’s chat, if the chat is activated, prepended with the server prefix.
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
     * Update the server brand field in the debug menu (F3) of the player (third line in 1.15 debug screen).
	 * Supports legacy section format but no line break.
     * @param brand the server brand to send to the client.
     */
	void sendServerBrand(String brand);
	
	
	
	
	
	
	
	
	/*
	 * Client options
	 */


	/**
	 * Gets the current client options of this player.
	 * @return the current client options of this player.
	 */
	ClientOptions getClientOptions();

	/**
	 * Interface providing various configuration values of the Minecraft client.
	 */
	interface ClientOptions {

		/**
		 * The language of the client interface.
		 * @return language of the client interface.
		 */
		Locale getLocale();

		/**
		 * The client view distance, in chunks.
		 * @return client view distance, in chunks.
		 */
		int getViewDistance();


		/**
		 * If the chat displays the text colors.
		 * @return true if the chat displays the text colors, false otherwise.
		 */
		boolean hasChatColorEnabled();
		
		/**
		 * Tells if the client is configured to completely hide the chat to the player. When this is the case, nothing
		 * is displayed in the chat box, and the player can’t send any message or command.
		 * @return true if the chat is fully hidden, false otherwise.
		 */
		boolean isChatHidden();

		/**
		 * Tells if the client is configured to display the chat normally. When this is the case, chat messages and
		 * system messages are displayed in the chat box, and the player can send messages and commands.
		 * @return true if the chat is fully visible, false otherwise.
		 */
		boolean isChatFullyVisible();

		/**
		 * Tells if the client is configured to only display system messages in the chat.
		 * When this is the case, chat messages are hidden but system messages are visible in the chat box, and the
		 * player can only send commands.
		 * @return true if the chat is visible but only shows system messages, false otherwise.
		 */
		boolean isChatOnlyDisplayingSystemMessages();
		
		
		
		/**
		 * Tells if the client has configured the main hand on the left.
		 * @return true if the player’s character is left handed, false otherwise.
		 */
		boolean isLeftHanded();

		/**
		 * Tells if the client has configured the main hand on the right.
		 * @return true if the player’s character is right handed, false otherwise.
		 */
		boolean isRightHanded();
		
		
		/**
		 * Tells if the client has enabled the filtering of texts on sign and book titles.
		 * @return true if the client filters swearing in texts, false otherwise.
		 */
		boolean isTextFilteringEnabled();
		
		
		/**
		 * Tells if the client allows the server to list their player name in the multiplayer menu.
		 * <b>To respect the player privacy’s configuration, this configuration value must be verified when generating
		 * custom ping response packet (MOTD in multiplayer server’s menu) that includes player names.</b>
		 * @return true if the client allows the server to list their player name in the multiplayer menu, false
		 *         otherwise.
		 */
		boolean allowsServerListing();


		/**
		 * Tells if the cape is enabled on the player’s skin.
		 * @return true if the cape is enabled on the player’s skin, false otherwise.
		 */
		boolean hasSkinCapeEnabled();

		/**
		 * Tells if the jacket is enabled on the player’s skin.
		 * @return true if the jacket is enabled on the player’s skin, false otherwise.
		 */
		boolean hasSkinJacketEnabled();

		/**
		 * Tells if the left sleeve is enabled on the player’s skin.
		 * @return true if the left sleeve is enabled on the player’s skin, false otherwise.
		 */
		boolean hasSkinLeftSleeveEnabled();

		/**
		 * Tells if the right sleeve is enabled on the player’s skin.
		 * @return true if the right sleeve is enabled on the player’s skin, false otherwise.
		 */
		boolean hasSkinRightSleeveEnabled();

		/**
		 * Tells if the left pants is enabled on the player’s skin.
		 * @return true if the left pants is enabled on the player’s skin, false otherwise.
		 */
		boolean hasSkinLeftPantsEnabled();

		/**
		 * Tells if the right pants is enabled on the player’s skin.
		 * @return true if the right pants is enabled on the player’s skin, false otherwise.
		 */
		boolean hasSkinRightPantsEnabled();

		/**
		 * Tells if the hat is enabled on the player’s skin.
		 * @return true if the hat is enabled on the player’s skin, false otherwise.
		 */
		boolean hasSkinHatsEnabled();
		
	}
	
	/**
	 * Tells if the player can send chat messages or receive chat messages from other players, according to their client
	 * configuration.
	 * <p>
	 * Chat messages represent public communication between players. By default, it only include actual chat message.
	 * This method may be used in commands like /me, /afk or the login/logout broadcasted messages.
	 * @return true if the player can send chat messages or receive chat messages from other players, false otherwise.
	 */
	default boolean canChat() {
		return getClientOptions().isChatFullyVisible();
	}
	

}
