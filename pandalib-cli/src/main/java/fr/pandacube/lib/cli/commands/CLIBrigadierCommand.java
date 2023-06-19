package fr.pandacube.lib.cli.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.pandacube.lib.commands.BrigadierCommand;
import fr.pandacube.lib.commands.SuggestionsSupplier;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Abstract class that holds the logic of a specific command to be registered in {@link CLIBrigadierDispatcher}.
 */
public abstract class CLIBrigadierCommand extends BrigadierCommand<CLICommandSender> {

	/**
	 * Instantiate this command instance.
	 */
	public CLIBrigadierCommand() {
		LiteralCommandNode<CLICommandSender> commandNode = buildCommand().build();
		postBuildCommand(commandNode);
		String[] aliases = getAliases();
		if (aliases == null)
			aliases = new String[0];

		CLIBrigadierDispatcher.instance.register(commandNode);

		
		for (String alias : aliases) {
			CLIBrigadierDispatcher.instance.register(literal(alias)
					.requires(commandNode.getRequirement())
					.executes(commandNode.getCommand())
					.redirect(commandNode)
					.build()
			);
		}
	}
	
	protected abstract LiteralArgumentBuilder<CLICommandSender> buildCommand();
	
	protected String[] getAliases() {
		return new String[0];
	}





	public boolean isPlayer(CLICommandSender sender) {
		return sender.isPlayer();
	}

	public boolean isConsole(CLICommandSender sender) {
		return sender.isConsole();
	}

	public Predicate<CLICommandSender> hasPermission(String permission) {
		return sender -> sender.hasPermission(permission);
	}





	/**
	 * Wraps the provided {@link SuggestionsSupplier} into a Brigadier’s {@link SuggestionProvider}.
	 * @param suggestions the suggestions to wrap.
	 * @return a {@link SuggestionProvider} generating the suggestions from the provided {@link SuggestionsSupplier}.
	 */
	protected SuggestionProvider<CLICommandSender> wrapSuggestions(SuggestionsSupplier<CLICommandSender> suggestions) {
		return wrapSuggestions(suggestions, Function.identity());
	}

	
	
	
	
	
	
	
	
}