package fr.pandacube.lib.cli.commands;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;

import fr.pandacube.lib.commands.BrigadierCommand;
import fr.pandacube.lib.commands.BrigadierSuggestionsUtil;
import fr.pandacube.lib.commands.SuggestionsSupplier;
import fr.pandacube.lib.util.Log;

public abstract class CLIBrigadierCommand extends BrigadierCommand<Object> {

	public CLIBrigadierCommand() {
		LiteralCommandNode<Object> commandNode = buildCommand().build();
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
	
	protected abstract LiteralArgumentBuilder<Object> buildCommand();
	
	protected String[] getAliases() {
		return new String[0];
	}





	public boolean isPlayer(Object sender) {
		return false;
	}

	public boolean isConsole(Object sender) {
		return true;
	}

	public Predicate<Object> hasPermission(String permission) {
		return sender -> true;
	}





	protected SuggestionProvider<Object> wrapSuggestions(SuggestionsSupplier<Object> suggestions) {
		return wrapSuggestions(suggestions, Function.identity());
	}

	
	
	
	
	
	
	
	
}