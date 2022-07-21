package fr.pandacube.lib.players.permissible;

import java.util.Objects;
import java.util.UUID;

import net.kyori.adventure.text.ComponentLike;

import fr.pandacube.lib.chat.ChatStatic;
import fr.pandacube.lib.players.standalone.StandalonePlayerManager;

public abstract class PermissiblePlayerManager<OP extends PermissibleOnlinePlayer, OF extends PermissibleOffPlayer> extends StandalonePlayerManager<OP, OF> {
	private static PermissiblePlayerManager<?, ?> instance;

	public static synchronized PermissiblePlayerManager<?, ?> getInstance() {
		return instance;
	}

	private static synchronized void setInstance(PermissiblePlayerManager<?, ?> newInstance) {
		if (instance != null) {
			throw new IllegalStateException("cannot have multiple instance of PlayerManager");
		}
		instance = newInstance;
	}
	
	public PermissiblePlayerManager() {
		super();
		setInstance(this);
	}

	@Override
	public void broadcastMessage(ComponentLike message, boolean prefix, boolean console, String permission, UUID sourcePlayer) {
		Objects.requireNonNull(message, "message cannot be null");

		if (prefix)
			message = ChatStatic.prefixedAndColored(message.asComponent());

		for (PermissibleOnlinePlayer op : getAll()) {
			if (permission != null && !(op.hasPermission(permission))) continue;

			if (sourcePlayer != null)
				op.sendMessage(message, sourcePlayer); // CHAT message with UUID
			else
				op.sendMessage(message); // SYSTEM message
		}
		if (console)
			sendMessageToConsole(message.asComponent());
	}


	/*
	 * Message broadcasting
	 */

	// ComponentLike message
	// boolean prefix
	// boolean console = (permission == null)
	// String permission = null
	// UUID sourcePlayer = null
	

	/**
	 * Broadcast a message to some or all players, and eventually to the console.
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @param console if the message must be displayed in the console.
	 * @param permission if not null, the message is only sent to player with this permission.
	 * @param sourcePlayer specifiy the eventual player that is the source of the message.
	 * 			If null, the message will be sent as a SYSTEM chat message.
	 * 			If not null, the message will be sent as a CHAT message, and will not be sent
	 * 			to players ignoring the provided player (if implemented).
	 * 			
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(ComponentLike message, boolean prefix, boolean console, String permission, UUID sourcePlayer) {
		getInstance().broadcastMessage(message, prefix, console, permission, sourcePlayer);
	}

	/**
	 * Broadcast a message to some or all players, and eventually to the console.
	 * <p>
	 * This method assumes this message is not caused by a specific player. To specify the source player, use
	 * {@link #broadcast(ComponentLike, boolean, boolean, String, UUID)}.
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @param console if the message must be displayed in the console.
	 * @param permission if not null, the message is only sent to player with this permission.
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(ComponentLike message, boolean prefix, boolean console, String permission) {
		broadcast(message, prefix, console, permission, null);
	}

	/**
	 * Broadcast a message to some or all players, and eventually to the console.
	 * <p>
	 * This method assumes this message is not caused by a specific player. To specify the source player, use
	 * {@link #broadcast(ComponentLike, boolean, String, UUID)}.
	 * <p>
	 * This method decides to send the message to the console depending on whether {@code permission}
	 * is null (will send to console) or not (will not send to console). To specify this behaviour, use
	 * {@link #broadcast(ComponentLike, boolean, boolean, String)}.
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @param permission if not null, the message is only sent to player with this permission (but not to console).
	 * 			If null, the message will be sent to all players and to console.
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(ComponentLike message, boolean prefix, String permission) {
		broadcast(message, prefix, (permission == null), permission, null);
	}

	/**
	 * Broadcast a message to all players, and to the console.
	 * <p>
	 * This method sends the message to the console. To change this behaviour, use
	 * {@link #broadcast(ComponentLike, boolean, boolean, String, UUID)}.
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @param permission if not null, the message is only sent to player with this permission (but not to console).
	 * 			If null, the message will be sent to all players and to console.
	 * @param sourcePlayer specifiy the eventual player that is the source of the message.
	 * 			If null, the message will be sent as a SYSTEM chat message.
	 * 			If not null, the message will be sent as a CHAT message, and will not be sent
	 * 			to players ignoring the provided player (if implemented).
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(ComponentLike message, boolean prefix, String permission, UUID sourcePlayer) {
		broadcast(message, prefix, true, permission, sourcePlayer);
	}
	
	
	
	
	
	
}
