package fr.pandacube.lib.cli.commands;

import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import fr.pandacube.lib.commands.BrigadierDispatcher;
import jline.console.completer.Completer;
import net.kyori.adventure.text.ComponentLike;

import java.util.List;

/**
 * Implementation of {@link BrigadierDispatcher} that integrates the commands into the JLine CLI interface.
 */
public class CLIBrigadierDispatcher extends BrigadierDispatcher<CLICommandSender> implements Completer {

	/**
	 * The instance of {@link CLIBrigadierDispatcher}.
	 */
	public static final CLIBrigadierDispatcher instance = new CLIBrigadierDispatcher();


	/**
	 * The sender for the console itself.
	 */
	public static final CLICommandSender CLI_CONSOLE_COMMAND_SENDER = new CLIConsoleCommandSender();


	/**
	 * Executes the provided command as the console.
	 * @param commandWithoutSlash the command, without the eventual slash at the begining.
	 * @return the value returned by the executed command.
	 */
	public int execute(String commandWithoutSlash) {
		return execute(CLI_CONSOLE_COMMAND_SENDER, commandWithoutSlash);
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
	 * Gets the suggestions for the currently being typed command, as the console.
	 * @param buffer the command that is being typed.
	 * @return the suggestions for the currently being typed command.
	 */
	public Suggestions getSuggestions(String buffer) {
		return getSuggestions(CLI_CONSOLE_COMMAND_SENDER, buffer);
	}


	@Override
	protected void sendSenderMessage(CLICommandSender sender, ComponentLike message) {
		sender.sendMessage(message);
	}
}
