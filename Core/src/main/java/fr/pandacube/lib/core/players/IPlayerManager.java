package fr.pandacube.lib.core.players;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import fr.pandacube.lib.core.chat.Chat;
import fr.pandacube.lib.core.db.DB;
import fr.pandacube.lib.core.db.DBInitTableException;
import fr.pandacube.lib.core.util.Log;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;

public abstract class IPlayerManager<OP extends IOnlinePlayer, OF extends IOffPlayer> {
	private static IPlayerManager<?, ?> instance;
	public static synchronized IPlayerManager<?, ?> getInstance() { return instance; }
	
	
	private Map<UUID, OP> onlinePlayers = Collections.synchronizedMap(new HashMap<>());
	
	private LoadingCache<UUID, OF> offlinePlayers = CacheBuilder.newBuilder()
			.expireAfterWrite(10, TimeUnit.MINUTES)
			.build(CacheLoader.from(pId -> newOffPlayerInstance(pId)));
	
	
	public IPlayerManager() throws DBInitTableException {
		synchronized (IPlayerManager.class) {
			instance = this;
		}

		DB.initTable(SQLPlayer.class);
		DB.initTable(SQLPlayerConfig.class);
		DB.initTable(SQLPlayerIgnore.class);
		DB.initTable(SQLPlayerNameHistory.class);
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

	public List<OP> getAllNotVanished() {
		List<OP> players = getAll();
		players.removeIf(op -> op.isVanished());
		return players;
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
		} catch (Exception e) {
			Log.severe("Cannot cache Offline player instance", e);
			return newOffPlayerInstance(p);
		}
	}
	
	
	
	
	protected abstract OF newOffPlayerInstance(UUID p);
	
	protected abstract void sendMessageToConsole(Component message);
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Deprecated
	public static BaseComponent prefixedAndColored(BaseComponent message) {
		return prefixedAndColored(Chat.chatComponent(message)).get();
	}
	
	public static Component prefixedAndColored(Component message) {
		return prefixedAndColored(Chat.chatComponent(message)).getAdv();
	}
	
	public static Chat prefixedAndColored(Chat message) {
		return Chat.chat()
				.color(Chat.getConfig().broadcastColor)
				.then(Chat.getConfig().prefix.get())
				.then(message);
	}
	
	
	
	
	/*
	 * Message broadcasting
	 */
	
	// BaseComponent/Chat/String message
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
	 * 			to players ignoring the provided player.
	 * 			
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(Component message, boolean prefix, boolean console, String permission, UUID sourcePlayer) {
		Objects.requireNonNull(message, "message cannot be null");
		
		IOffPlayer oSourcePlayer = getInstance().getOffline(sourcePlayer);
		
		if (prefix)
			message = prefixedAndColored(message);

		for (IOnlinePlayer op : getInstance().getAll()) {
			if (permission != null && !(op.hasPermission(permission))) continue;
			if (sourcePlayer != null && op.isIgnoring(oSourcePlayer))
				continue;
			
			if (sourcePlayer != null) {
				if (op.canIgnore(oSourcePlayer)) {
					op.sendMessage(message, sourcePlayer); // CHAT message with UUID
				}
				else {
					op.sendMessage(message, new UUID(0, 0)); // CHAT message without UUID
				}
			}
			else
				op.sendMessage(message); // SYSTEM message
		}

		if (console)
			getInstance().sendMessageToConsole(message);
	}

	/**
	 * Broadcast a message to some or all players, and eventually to the console.
	 * <p>
	 * This method assumes this message is not caused by a specific player. To specify the source player, use
	 * {@link #broadcast(BaseComponent, boolean, boolean, String, UUID)}.
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @param console if the message must be displayed in the console.
	 * @param permission if not null, the message is only sent to player with this permission.
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(Component message, boolean prefix, boolean console, String permission) {
		broadcast(message, prefix, console, permission, null);
	}

	/**
	 * Broadcast a message to all players, and eventually to the console.
	 * <p>
	 * This method does not restrict the reception of the message to a specific permission. If you
	 * want to specify a permission, use {@link #broadcast(BaseComponent, boolean, boolean, String, UUID)}.
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @param console if the message must be displayed in the console.
	 * @param sourcePlayer specifiy the eventual player that is the source of the message.
	 * 			If null, the message will be sent as a SYSTEM chat message.
	 * 			If not null, the message will be sent as a CHAT message, and will not be sent
	 * 			to players ignoring the provided player.
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(Component message, boolean prefix, boolean console, UUID sourcePlayer) {
		broadcast(message, prefix, console, null, sourcePlayer);
	}

	/**
	 * Broadcast a message to all players, and eventually to the console.
	 * <p>
	 * This method does not restrict the reception of the message to a specific permission. If you
	 * want to specify a permission, use {@link #broadcast(BaseComponent, boolean, boolean, String)}.
	 * <p>
	 * This method assumes this message is not caused by a specific player. To specify the source player, use
	 * {@link #broadcast(BaseComponent, boolean, boolean, UUID)}.
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @param console if the message must be displayed in the console.
	 * @throws IllegalArgumentException if message is null.
	 */
	@Deprecated
	public static void broadcast(BaseComponent message, boolean prefix, boolean console) {
		broadcast(Chat.toAdventure(message), prefix, console, null, null);
	}

	/**
	 * Broadcast a message to all players, and eventually to the console.
	 * <p>
	 * This method does not restrict the reception of the message to a specific permission. If you
	 * want to specify a permission, use {@link #broadcast(BaseComponent, boolean, boolean, String)}.
	 * <p>
	 * This method assumes this message is not caused by a specific player. To specify the source player, use
	 * {@link #broadcast(BaseComponent, boolean, boolean, UUID)}.
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @param console if the message must be displayed in the console.
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(Component message, boolean prefix, boolean console) {
		broadcast(message, prefix, console, null, null);
	}

	/**
	 * Broadcast a message to some or all players, and eventually to the console.
	 * <p>
	 * This method assumes this message is not caused by a specific player. To specify the source player, use
	 * {@link #broadcast(BaseComponent, boolean, String, UUID)}.
	 * <p>
	 * This method decides to send the message to the console depending on whether {@code permission}
	 * is null (will send to console) or not (will not send to console). To specify this behaviour, use
	 * {@link #broadcast(BaseComponent, boolean, boolean, String)}.
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @param permission if not null, the message is only sent to player with this permission (but not to console).
	 * 			If null, the message will be sent to all players and to console.
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(Component message, boolean prefix, String permission) {
		broadcast(message, prefix, (permission == null), permission, null);
	}

	/**
	 * Broadcast a message to all players, and to the console.
	 * <p>
	 * This method does not restrict the reception of the message to a specific permission. If you
	 * want to specify a permission, use {@link #broadcast(BaseComponent, boolean, String, UUID)}.
	 * <p>
	 * This method sends the message to the console. To change this behaviour, use
	 * {@link #broadcast(BaseComponent, boolean, boolean, UUID)}.
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @param sourcePlayer specifiy the eventual player that is the source of the message.
	 * 			If null, the message will be sent as a SYSTEM chat message.
	 * 			If not null, the message will be sent as a CHAT message, and will not be sent
	 * 			to players ignoring the provided player.
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(Component message, boolean prefix, UUID sourcePlayer) {
		broadcast(message, prefix, true, null, sourcePlayer);
	}

	/**
	 * Broadcast a message to all players, and to the console.
	 * <p>
	 * This method assumes this message is not caused by a specific player. To specify the source player, use
	 * {@link #broadcast(BaseComponent, boolean, UUID)}.
	 * <p>
	 * This method does not restrict the reception of the message to a specific permission. If you
	 * want to specify a permission, use {@link #broadcast(BaseComponent, boolean, String)}.
	 * <p>
	 * This method sends the message to the console. To change this behaviour, use
	 * {@link #broadcast(BaseComponent, boolean, boolean)}.
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(Component message, boolean prefix) {
		broadcast(message, prefix, true, null, null);
	}
	
	
	

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
	 * 			to players ignoring the provided player.
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(Chat message, boolean prefix, boolean console, String permission, UUID sourcePlayer) {
		Objects.requireNonNull(message, "message cannot be null");
		broadcast(message.getAdv(), prefix, console, permission, sourcePlayer);
	}

	/**
	 * Broadcast a message to some or all players, and eventually to the console.
	 * <p>
	 * This method assumes this message is not caused by a specific player. To specify the source player, use
	 * {@link #broadcast(BaseComponent, boolean, boolean, String, UUID)}.
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @param console if the message must be displayed in the console.
	 * @param permission if not null, the message is only sent to player with this permission.
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(Chat message, boolean prefix, boolean console, String permission) {
		broadcast(message, prefix, console, permission, null);
	}

	/**
	 * Broadcast a message to all players, and eventually to the console.
	 * <p>
	 * This method does not restrict the reception of the message to a specific permission. If you
	 * want to specify a permission, use {@link #broadcast(BaseComponent, boolean, boolean, String, UUID)}.
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @param console if the message must be displayed in the console.
	 * @param sourcePlayer specifiy the eventual player that is the source of the message.
	 * 			If null, the message will be sent as a SYSTEM chat message.
	 * 			If not null, the message will be sent as a CHAT message, and will not be sent
	 * 			to players ignoring the provided player.
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(Chat message, boolean prefix, boolean console, UUID sourcePlayer) {
		broadcast(message, prefix, console, null, sourcePlayer);
	}

	/**
	 * Broadcast a message to all players, and eventually to the console.
	 * <p>
	 * This method does not restrict the reception of the message to a specific permission. If you
	 * want to specify a permission, use {@link #broadcast(BaseComponent, boolean, boolean, String)}.
	 * <p>
	 * This method assumes this message is not caused by a specific player. To specify the source player, use
	 * {@link #broadcast(BaseComponent, boolean, boolean, UUID)}.
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @param console if the message must be displayed in the console.
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(Chat message, boolean prefix, boolean console) {
		broadcast(message, prefix, console, null, null);
	}

	/**
	 * Broadcast a message to some or all players, and eventually to the console.
	 * <p>
	 * This method assumes this message is not caused by a specific player. To specify the source player, use
	 * {@link #broadcast(BaseComponent, boolean, String, UUID)}.
	 * <p>
	 * This method decides to send the message to the console depending on whether {@code permission}
	 * is null (will send to console) or not (will not send to console). To specify this behaviour, use
	 * {@link #broadcast(BaseComponent, boolean, boolean, String)}.
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @param permission if not null, the message is only sent to player with this permission (but not to console).
	 * 			If null, the message will be sent to all players and to console.
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(Chat message, boolean prefix, String permission) {
		broadcast(message, prefix, (permission == null), permission, null);
	}

	/**
	 * Broadcast a message to all players, and to the console.
	 * <p>
	 * This method does not restrict the reception of the message to a specific permission. If you
	 * want to specify a permission, use {@link #broadcast(BaseComponent, boolean, String, UUID)}.
	 * <p>
	 * This method sends the message to the console. To change this behaviour, use
	 * {@link #broadcast(BaseComponent, boolean, boolean, UUID)}.
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @param sourcePlayer specifiy the eventual player that is the source of the message.
	 * 			If null, the message will be sent as a SYSTEM chat message.
	 * 			If not null, the message will be sent as a CHAT message, and will not be sent
	 * 			to players ignoring the provided player.
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(Chat message, boolean prefix, UUID sourcePlayer) {
		broadcast(message, prefix, true, null, sourcePlayer);
	}

	/**
	 * Broadcast a message to all players, and to the console.
	 * <p>
	 * This method assumes this message is not caused by a specific player. To specify the source player, use
	 * {@link #broadcast(BaseComponent, boolean, UUID)}.
	 * <p>
	 * This method does not restrict the reception of the message to a specific permission. If you
	 * want to specify a permission, use {@link #broadcast(BaseComponent, boolean, String)}.
	 * <p>
	 * This method sends the message to the console. To change this behaviour, use
	 * {@link #broadcast(BaseComponent, boolean, boolean)}.
	 *
	 * @param message the message to send.
	 * @param prefix if the server prefix will be prepended to the message.
	 * @throws IllegalArgumentException if message is null.
	 */
	public static void broadcast(Chat message, boolean prefix) {
		broadcast(message, prefix, true, null, null);
	}
	
	
	
	
	
	
}
