package fr.pandacube.lib.bungee.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

import fr.pandacube.lib.core.chat.ChatStatic;
import fr.pandacube.lib.core.commands.SuggestionsSupplier;
import fr.pandacube.lib.core.util.Log;
import fr.pandacube.lib.core.util.ReflexionUtil;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.command.ConsoleCommandSender;

public abstract class BrigadierCommand extends ChatStatic {
	
	private LiteralCommandNode<CommandSender> commandNode;
	
	protected BrigadierDispatcher dispatcher;
	
	public BrigadierCommand() {
		if (BrigadierDispatcher.getInstance() == null) {
			throw new IllegalStateException("BrigadierDispatcher is not yet initialized.");
		}
		dispatcher = BrigadierDispatcher.getInstance();
		
		LiteralArgumentBuilder<CommandSender> builder;
		String[] aliases;
		
		try {
			builder = buildCommand();
			aliases = getAliases();
		} catch (Exception e) {
			Log.severe("Exception encountered when building Brigadier command " + getClass().getName(), e);
			return;
		}
		if (aliases == null)
			aliases = new String[0];
		
		commandNode = dispatcher.register(builder);
		
		// still have to be registered for console
		BungeeCord.getInstance().getPluginManager().registerCommand(dispatcher.plugin, new CommandRelay(commandNode.getLiteral()));
		
		for (String alias : aliases) {
			dispatcher.register(literal(alias)
					.requires(commandNode.getRequirement())
					.executes(commandNode.getCommand())
					.redirect(commandNode)
			);
			
			BungeeCord.getInstance().getPluginManager().registerCommand(dispatcher.plugin, new CommandRelay(alias));
		}
		
	}
	
	private class CommandRelay extends Command implements TabExecutor {
		private String alias;
		public CommandRelay(String alias) {
			super(alias);
			this.alias = alias;
		}
		@Override
		public void execute(CommandSender sender, String[] args) {
			dispatcher.execute(sender, alias + (args.length == 0 ? "" : (" " + String.join(" ", args))));
		}
		@Override
		public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
			
			String cursor = "/" + alias + " " + String.join(" ", args);

            StringRange supportedRange = StringRange.between(cursor.lastIndexOf(' ') + 1, cursor.length());
            
            Suggestions suggestions = dispatcher.getSuggestions(sender, cursor.substring(1));
            if (!suggestions.getRange().equals(supportedRange))
            	return Collections.emptyList();
            
			return suggestions.getList()
					.stream()
					.filter(s -> s.getRange().equals(supportedRange))
					.map(s -> s.getText())
					.collect(Collectors.toList());
		}
	}
	
	protected abstract LiteralArgumentBuilder<CommandSender> buildCommand();
	
	protected String[] getAliases() {
		return new String[0];
	}
	
	
	
	
	public static LiteralArgumentBuilder<CommandSender> literal(String name) {
		return LiteralArgumentBuilder.literal(name);
	}
	
	public static <T> RequiredArgumentBuilder<CommandSender, T> argument(String name, ArgumentType<T> type) {
		return RequiredArgumentBuilder.argument(name, type);
	}
	
	public static Predicate<CommandSender> hasPermission(String permission) {
		return sender -> sender.hasPermission(permission);
	}
	
	public static Predicate<CommandSender> isPlayer() {
		return sender -> isPlayer(sender);
	}
	
	public static boolean isPlayer(CommandSender sender) {
		return sender instanceof ProxiedPlayer;
	}
	
	public static Predicate<CommandSender> isConsole() {
		return sender -> isConsole(sender);
	}
	
	public static boolean isConsole(CommandSender sender) {
		return sender instanceof ConsoleCommandSender;
	}
	
	
	public static boolean isLiteralParsed(CommandContext<CommandSender> context, String literal) {
		for (ParsedCommandNode<CommandSender> node : context.getNodes()) {
			if (!(node.getNode() instanceof LiteralCommandNode))
				continue;
			if (((LiteralCommandNode<CommandSender>)node.getNode()).getLiteral().equals(literal))
				return true;
		}
		return false;
	}
	
	public static <T> T tryGetArgument(CommandContext<CommandSender> context, String argument, Class<T> type) {
		return tryGetArgument(context, argument, type, null);
	}
	
	public static <T> T tryGetArgument(CommandContext<CommandSender> context, String argument, Class<T> type, T deflt) {
		try {
			return context.getArgument(argument, type);
		} catch (IllegalArgumentException e) {
			return deflt;
		}
	}
	


	
	protected static SuggestionProvider<CommandSender> wrapSuggestions(SuggestionsSupplier<CommandSender> suggestions) {
		return (context, builder) -> {
			CommandSender sender = context.getSource();
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
				Log.severe("Error while tab-completing '" + message + "' for " + sender.getName(), e);
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
			return (List<Suggestion>) ReflexionUtil.getDeclaredFieldValue(SuggestionsBuilder.class, builder, "result");
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	
	
	
}