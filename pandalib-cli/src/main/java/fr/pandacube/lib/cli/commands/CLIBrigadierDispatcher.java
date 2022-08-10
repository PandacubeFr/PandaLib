package fr.pandacube.lib.cli.commands;

import java.util.List;

import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;

import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.commands.BrigadierDispatcher;
import fr.pandacube.lib.util.Log;
import jline.console.completer.Completer;
import net.kyori.adventure.text.ComponentLike;

/**
 * Implementation of {@link BrigadierDispatcher} that integrates the commands into the JLine CLI interface.
 */
public class CLIBrigadierDispatcher extends BrigadierDispatcher<Object> implements Completer {

	/**
	 * The instance of {@link CLIBrigadierDispatcher}.
	 */
	public static final CLIBrigadierDispatcher instance = new CLIBrigadierDispatcher();


	private static final Object sender = new Object();


	/**
	 * Executes the provided command.
	 * @param commandWithoutSlash the command, without the eventual slash at the begining.
	 * @return the value returned by the executed command.
	 */
	public int execute(String commandWithoutSlash) {
		return execute(sender, commandWithoutSlash);
	}
	
	
	@Override
	public int complete(String buffer, int cursor, List<CharSequence> candidates) {
		
		String bufferBeforeCursor = buffer.substring(0, cursor);
		
		Suggestions completeResult = getSuggestions(bufferBeforeCursor);
		
		completeResult.getList().stream()
				.map(Suggestion::getText)
				.forEach(candidates::add);
		
		return completeResult.getRange().getStart();
	}

	/**
	 * Gets the suggestions for the currently being typed command.
	 * @param buffer the command that is being typed.
	 * @return the suggestions for the currently being typed command.
	 */
	public Suggestions getSuggestions(String buffer) {
		return getSuggestions(sender, buffer);
	}


	@Override
	protected void sendSenderMessage(Object sender, ComponentLike message) {
		Log.info(Chat.chatComponent(message).getLegacyText());
	}
}
