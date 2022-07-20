package fr.pandacube.lib.core.players;

import java.util.UUID;

import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.db.DBInitTableException;
import fr.pandacube.lib.util.Log;
import net.kyori.adventure.text.Component;

/**
 * A standalone player manager, using an implementation of {@link IPlayerManager}
 * that does not manage online players. This is used to ease access to players data
 * on standalone applications (web api, discord bot, ...)
 *
 */
/* package */ class StandalonePlayerManager extends IPlayerManager<IOnlinePlayer, StandaloneOffPlayer> {

	public StandalonePlayerManager() throws DBInitTableException {
		super();
	}

	@Override
	protected StandaloneOffPlayer newOffPlayerInstance(UUID p) {
		return new StandaloneOffPlayer(p);
	}
	
	@Override
	protected void sendMessageToConsole(Component message) {
		Log.info(Chat.chatComponent(message).getLegacyText());
	}
	
}
