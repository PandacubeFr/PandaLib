package fr.pandacube.lib.bungee.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.pandacube.lib.commands.BrigadierCommand;
import fr.pandacube.lib.commands.SuggestionsSupplier;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.util.Log;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class BungeeBrigadierCommand extends BrigadierCommand<CommandSender> {

	protected BungeeBrigadierDispatcher dispatcher;
	
	public BungeeBrigadierCommand(BungeeBrigadierDispatcher d) {
		if (d == null) {
			throw new IllegalStateException("BungeeBrigadierDispatcher not provided.");
		}
		dispatcher = d;
		
		LiteralCommandNode<CommandSender> commandNode;
		String[] aliases;
		
		try {
			commandNode = buildCommand().build();
			postBuildCommand(commandNode);
			aliases = getAliases();
		} catch (Exception e) {
			Log.severe("Exception encountered when building Brigadier command " + getClass().getName(), e);
			return;
		}
		if (aliases == null)
			aliases = new String[0];

		dispatcher.register(commandNode);
		
		// still have to be registered for console
		ProxyServer.getInstance().getPluginManager().registerCommand(dispatcher.plugin, new CommandRelay(commandNode.getLiteral()));
		
		for (String alias : aliases) {
			dispatcher.register(literal(alias)
					.requires(commandNode.getRequirement())
					.executes(commandNode.getCommand())
					.redirect(commandNode)
					.build()
			);

			ProxyServer.getInstance().getPluginManager().registerCommand(dispatcher.plugin, new CommandRelay(alias));
		}
		
	}
	
	private class CommandRelay extends Command implements TabExecutor {
		private final String alias;
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
					.map(Suggestion::getText)
					.collect(Collectors.toList());
		}
	}










	public boolean isConsole(CommandSender sender) {
		return ProxyServer.getInstance().getConsole().equals(sender);
	}
	public boolean isPlayer(CommandSender sender) {
		return sender instanceof ProxiedPlayer;
	}
	public Predicate<CommandSender> hasPermission(String permission) {
		return sender -> sender.hasPermission(permission);
	}





	protected SuggestionProvider<CommandSender> wrapSuggestions(SuggestionsSupplier<CommandSender> suggestions) {
		return wrapSuggestions(suggestions, Function.identity());
	}


}