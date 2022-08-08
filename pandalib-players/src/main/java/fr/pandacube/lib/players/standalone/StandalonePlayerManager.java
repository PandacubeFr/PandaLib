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

public abstract class StandalonePlayerManager<OP extends StandaloneOnlinePlayer, OF extends StandaloneOffPlayer> {
	private static StandalonePlayerManager<?, ?> instance;
	
	public static synchronized StandalonePlayerManager<?, ?> getInstance() {
		return instance;
	}
	
	private static synchronized void setInstance(StandalonePlayerManager<?, ?> newInstance) {
		if (instance != null) {
			throw new IllegalStateException("cannot have multiple instance of PlayerManager");
		}
		instance = newInstance;
	}
	
	
	
	
	private final Map<UUID, OP> onlinePlayers = Collections.synchronizedMap(new HashMap<>());
	
	private final LoadingCache<UUID, OF> offlinePlayers = CacheBuilder.newBuilder()
			.expireAfterWrite(10, TimeUnit.MINUTES)
			.build(CacheLoader.from(this::newOffPlayerInstance));
	
	
	public StandalonePlayerManager() {
		setInstance(this);
	}
	
	
	
	protected void addPlayer(OP p) {
		onlinePlayers.put(p.getUniqueId(), p);
		offlinePlayers.invalidate(p.getUniqueId());
	}
	
	protected OP removePlayer(UUID p) {
		return onlinePlayers.remove(p);
	}
	
	public OP get(UUID p) {
		return onlinePlayers.get(p);
	}
	
	public boolean isOnline(UUID p) {
		return onlinePlayers.containsKey(p);
	}
	
	public int getPlayerCount() {
		return onlinePlayers.size();
	}
	
	public List<OP> getAll() {
		return new ArrayList<>(onlinePlayers.values());
	}
	
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
	
	
	
	
	protected abstract OF newOffPlayerInstance(UUID p);
	
	protected abstract void sendMessageToConsole(Component message);
	


	public void broadcastMessage(ComponentLike message, boolean prefix, boolean console, String permission, UUID sourcePlayer) {
		Objects.requireNonNull(message, "message cannot be null");

		if (prefix)
			message = ChatStatic.prefixedAndColored(message.asComponent());

		for (StandaloneOnlinePlayer op : getAll()) {
			if (sourcePlayer != null)
				op.sendMessage(message, sourcePlayer); // CHAT message without UUID
			else
				op.sendMessage(message); // SYSTEM message
		}

		if (console)
			getInstance().sendMessageToConsole(message.asComponent());
	}
	
	
	
	
	
	
	
	

	
	
	
	
	/*
	 * Message broadcasting
	 */

	// ComponentLike message
	// boolean prefix
	// boolean console = (permission == null)
	// UUID sourcePlayer = null


	/**
	 * Broadcast a message to some or all players, and eventually to the console.
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
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @param console if the message must be displayed in the console.
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(ComponentLike message, boolean prefix, boolean console) {
		broadcast(message, prefix, console, null);
	}

	/**
	 * Broadcast a message to all players, and to the console.
	 * <p>
	 * This method sends the message to the console. To change this behaviour, use
	 * {@link #broadcast(ComponentLike, boolean, boolean, UUID)}.
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
		broadcast(message, prefix, true, sourcePlayer);
	}

	/**
	 * Broadcast a message to all players, and to the console.
	 * <p>
	 * This method assumes this message is not caused by a specific player. To specify the source player, use
	 * {@link #broadcast(ComponentLike, boolean, UUID)}.
	 * <p>
	 * This method sends the message to the console. To change this behaviour, use
	 * {@link #broadcast(ComponentLike, boolean, boolean)}.
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(ComponentLike message, boolean prefix) {
		broadcast(message, prefix, true, null);
	}
	
	
	
	
	
	
}
