package fr.pandacube.lib.players.standalone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;

import fr.pandacube.lib.chat.ChatStatic;

/**
 * Generic implementation of a player manager, handling instances of {@link AbstractOffPlayer} and
 * {@link AbstractOnlinePlayer}.
 * Subclasses should handle the addition and removal of {@link AbstractOnlinePlayer} instances according to the
 * currently online players.
 * @param <OP> the type of subclass of {@link AbstractOnlinePlayer}
 * @param <OF> the type of subclass of {@link AbstractOffPlayer}
 */
public abstract class AbstractPlayerManager<OP extends AbstractOnlinePlayer, OF extends AbstractOffPlayer> {
	private static AbstractPlayerManager<?, ?> instance;

	/**
	 * Gets the current instance of player manager.
	 * @return the player manager.
	 */
	public static synchronized AbstractPlayerManager<?, ?> getInstance() {
		return instance;
	}
	
	private static synchronized void setInstance(AbstractPlayerManager<?, ?> newInstance) {
		if (instance != null) {
			throw new IllegalStateException("cannot have multiple instance of PlayerManager");
		}
		instance = newInstance;
	}
	
	
	
	
	private final Map<UUID, OP> onlinePlayers = Collections.synchronizedMap(new HashMap<>());
	
	private final LoadingCache<UUID, OF> offlinePlayers = CacheBuilder.newBuilder()
			.expireAfterWrite(10, TimeUnit.MINUTES)
			.build(CacheLoader.from(this::newOffPlayerInstance));

	/**
	 * Creates a new instance of player manager.
	 */
	public AbstractPlayerManager() {
		setInstance(this);
	}


	/**
	 * Adds the provided player to this player manager.
	 * Usually this is called by a player join event handler.
	 * @param p the player to add.
	 */
	protected void addPlayer(OP p) {
		onlinePlayers.put(p.getUniqueId(), p);
		offlinePlayers.invalidate(p.getUniqueId());
	}

	/**
	 * Removes the player from this player manager.
	 * Usually this is called by a player quit event handler.
	 * @param p the UUID of the player to remove.
	 * @return the player that has been removed.
	 */
	protected OP removePlayer(UUID p) {
		return onlinePlayers.remove(p);
	}

	/**
	 * Get the online player that has the provided UUID.
	 * @param p the UUID of the player.
	 * @return the online player instance.
	 */
	public OP get(UUID p) {
		return onlinePlayers.get(p);
	}

	/**
	 * Tells if the provided player is online.
	 * @param p the UUID of the player.
	 * @return true if the provided player is online (that is when this player manager contains an online player
	 *         instance), false otherwise.
	 */
	public boolean isOnline(UUID p) {
		return onlinePlayers.containsKey(p);
	}

	/**
	 * Get the number of online player registered in this player manager.
	 * @return the number of online player registered in this player manager.
	 */
	public int getPlayerCount() {
		return onlinePlayers.size();
	}

	/**
	 * Get all the online players is this player manager.
	 * @return all the online players is this player manager.
	 */
	public List<OP> getAll() {
		return new ArrayList<>(onlinePlayers.values());
	}

	/**
	 * Returns an instance of {@link AbstractOffPlayer} corresponding to a player with the provided {@link UUID}.
	 * @param p the UUID of the player.
	 * @return an instance of {@link AbstractOffPlayer}. It can be a new instance, an {@link AbstractOnlinePlayer}
	 * instance if the player is online, or a cached instance of {@link AbstractOffPlayer}.
	 */
	public OF getOffline(UUID p) {
		if (p == null)
			return null;
		OP online = get(p);
		if (online != null) {
			offlinePlayers.invalidate(p);
			@SuppressWarnings("unchecked")
			OF ret = (OF) online;
			return ret;
		}
		// if not online
		try {
			return offlinePlayers.get(p); // load and cache new instance if necessary
		} catch (ExecutionException e) {
			throw new UncheckedExecutionException(e.getCause());
		}
	}


	/**
	 * Create a new instance of the appropriate subclass of {@link AbstractOffPlayer}.
	 * @param p the uuid of the player.
	 * @return the new instance of {@link AbstractOffPlayer}.
	 */
	protected abstract OF newOffPlayerInstance(UUID p);

	/**
	 * Abstract method to implement to send a message to the console.
	 * @param message the message to send to the console.
	 */
	protected abstract void sendMessageToConsole(Component message);



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
	 * @throws IllegalArgumentException if message is null.
	 * @implSpec subclasses may override this method, for instance to restrict the players being able to see the message
	 * (like /ignored players).
	 */
	public void broadcastMessage(ComponentLike message, boolean prefix, boolean console, String permission, UUID sourcePlayer) {
		Objects.requireNonNull(message, "message cannot be null");

		if (prefix)
			message = ChatStatic.prefixedAndColored(message.asComponent());

		for (OP op : getAll()) {
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



	/**
	 * Broadcast a message to some or all players, and eventually to the console.
	 * <p>
	 * This method doesn’t restrict to a specific permission. To change this behaviour, use
	 * {@link #broadcast(ComponentLike, boolean, boolean, String, UUID)}.
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @param console if the message must be displayed in the console.
	 * @param sourcePlayer specifiy the eventual player that is the source of the message.
	 * 			If null, the message will be sent as a SYSTEM chat message.
	 * 			If not null, the message will be sent as a CHAT message, and will not be sent
	 * 			to players ignoring the provided player (if implemented).
	 *
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(ComponentLike message, boolean prefix, boolean console, UUID sourcePlayer) {
		getInstance().broadcastMessage(message, prefix, console, null, sourcePlayer);
	}


	/**
	 * Broadcast a message to some or all players, and eventually to the console.
	 * <p>
	 * This method assumes this message is not caused by a specific player. To specify the source player, use
	 * {@link #broadcast(ComponentLike, boolean, boolean, UUID)}.
	 * <p>
	 * This method doesn’t restrict to a specific permission. To change this behaviour, use
	 * {@link #broadcast(ComponentLike, boolean, boolean, String)}.
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @param console if the message must be displayed in the console.
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(ComponentLike message, boolean prefix, boolean console) {
		broadcast(message, prefix, console, null, null);
	}

	/**
	 * Broadcast a message to all players, and to the console.
	 * <p>
	 * This method sends the message to the console. To change this behaviour, use
	 * {@link #broadcast(ComponentLike, boolean, boolean, UUID)}.
	 * <p>
	 * This method doesn’t restrict to a specific permission. To change this behaviour, use
	 * {@link #broadcast(ComponentLike, boolean, String, UUID)}.
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @param sourcePlayer specifiy the eventual player that is the source of the message.
	 * 			If null, the message will be sent as a SYSTEM chat message.
	 * 			If not null, the message will be sent as a CHAT message, and will not be sent
	 * 			to players ignoring the provided player (if implemented).
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(ComponentLike message, boolean prefix, UUID sourcePlayer) {
		broadcast(message, prefix, true, null, sourcePlayer);
	}

	/**
	 * Broadcast a message to all players, and to the console.
	 * <p>
	 * This method assumes this message is not caused by a specific player. To specify the source player, use
	 * {@link #broadcast(ComponentLike, boolean, UUID)}.
	 * <p>
	 * This method sends the message to the console. To change this behaviour, use
	 * {@link #broadcast(ComponentLike, boolean, boolean)}.
	 * <p>
	 * This method doesn’t restrict to a specific permission. To change this behaviour, use
	 * {@link #broadcast(ComponentLike, boolean, String)}.
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(ComponentLike message, boolean prefix) {
		broadcast(message, prefix, true, null, null);
	}
	
}
