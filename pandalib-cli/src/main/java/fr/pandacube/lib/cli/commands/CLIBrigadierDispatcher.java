package fr.pandacube.lib.cli.commands;

import java.util.List;

import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;

import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.commands.BrigadierDispatcher;
import fr.pandacube.lib.util.Log;
import jline.console.completer.Completer;
import net.kyori.adventure.text.ComponentLike;

public class CLIBrigadierDispatcher extends BrigadierDispatcher<Object> implements Completer {
	
	public static final CLIBrigadierDispatcher instance = new CLIBrigadierDispatcher();


	private static final Object sender = new Object();
	

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
	
	public Suggestions getSuggestions(String buffer) {
		return getSuggestions(sender, buffer);
	}


	@Override
	protected void sendSenderMessage(Object sender, ComponentLike message) {
		Log.info(Chat.chatComponent(message).getLegacyText());
	}
}
