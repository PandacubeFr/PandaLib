package fr.pandacube.lib.cli;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

import fr.pandacube.lib.core.chat.ChatStatic;
import fr.pandacube.lib.core.commands.SuggestionsSupplier;
import fr.pandacube.lib.core.util.Log;
import fr.pandacube.lib.core.util.Reflect;

public abstract class BrigadierCommand extends ChatStatic {
	
	private LiteralCommandNode<Object> commandNode;
	
	public BrigadierCommand() {
		LiteralArgumentBuilder<Object> builder = buildCommand();
		String[] aliases = getAliases();
		if (aliases == null)
			aliases = new String[0];
		
		commandNode = BrigadierDispatcher.instance.register(builder);
		
		
		for (String alias : aliases) {
			BrigadierDispatcher.instance.register(literal(alias)
					.requires(commandNode.getRequirement())
					.executes(commandNode.getCommand())
					.redirect(commandNode)
			);
			
		}
		
	}
	
	protected abstract LiteralArgumentBuilder<Object> buildCommand();
	
	protected String[] getAliases() {
		return new String[0];
	}
	
	
	
	
	public static LiteralArgumentBuilder<Object> literal(String name) {
		return LiteralArgumentBuilder.literal(name);
	}
	
	public static <T> RequiredArgumentBuilder<Object, T> argument(String name, ArgumentType<T> type) {
		return RequiredArgumentBuilder.argument(name, type);
	}
	
	
	public static boolean isLiteralParsed(CommandContext<Object> context, String literal) {
		for (ParsedCommandNode<Object> node : context.getNodes()) {
			if (!(node.getNode() instanceof LiteralCommandNode))
				continue;
			if (((LiteralCommandNode<Object>)node.getNode()).getLiteral().equals(literal))
				return true;
		}
		return false;
	}
	
	public static <T> T tryGetArgument(CommandContext<Object> context, String argument, Class<T> type) {
		return tryGetArgument(context, argument, type, null);
	}
	
	public static <T> T tryGetArgument(CommandContext<Object> context, String argument, Class<T> type, T deflt) {
		try {
			return context.getArgument(argument, type);
		} catch (IllegalArgumentException e) {
			return deflt;
		}
	}
	


	
	protected static SuggestionProvider<Object> wrapSuggestions(SuggestionsSupplier<Object> suggestions) {
		return (context, builder) -> {
			Object sender = context.getSource();
			String message = builder.getInput();
			try {
				int tokenStartPos = builder.getStart();
				
				List<String> results = Collections.emptyList();
			
				int firstSpacePos = message.indexOf(" ");
				String[] args = (firstSpacePos + 1 > tokenStartPos - 1) ? new String[0]
						: message.substring(firstSpacePos + 1, tokenStartPos - 1).split(" ", -1);
				args = Arrays.copyOf(args, args.length + 1);
				args[args.length - 1] = message.substring(tokenStartPos);
				
				results = suggestions.getSuggestions(sender, args.length - 1, args[args.length - 1], args);
			
				for (String s : results) {
					if (s != null)
						builder.suggest(s);
				}
			} catch (Throwable e) {
				Log.severe("Error while tab-completing '" + message/* + "' for " + sender.getName()*/, e);
			}
			return completableFutureSuggestionsKeepsOriginalOrdering(builder);
		};
	}
	
	
	
	
	public static CompletableFuture<Suggestions> completableFutureSuggestionsKeepsOriginalOrdering(SuggestionsBuilder builder) {
		return CompletableFuture.completedFuture(
				BrigadierDispatcher.createSuggestionsOriginalOrdering(builder.getInput(), getSuggestionsFromSuggestionsBuilder(builder))
		);
	}
	
	@SuppressWarnings("unchecked")
	private static List<Suggestion> getSuggestionsFromSuggestionsBuilder(SuggestionsBuilder builder) {
		try {
			return (List<Suggestion>) Reflect.ofClass(SuggestionsBuilder.class).field("result").getValue(builder);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	
	
	
	
	
}