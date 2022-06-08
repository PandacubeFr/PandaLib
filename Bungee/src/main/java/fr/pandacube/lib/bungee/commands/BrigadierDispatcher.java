package fr.pandacube.lib.bungee.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import fr.pandacube.lib.core.chat.Chat;
import fr.pandacube.lib.core.util.Log;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.CommandsDeclareEvent;
import net.md_5.bungee.api.event.TabCompleteRequestEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class BrigadierDispatcher implements Listener {
	
	private static BrigadierDispatcher instance = null;
	
	public static synchronized void init(Plugin plugin) {
		instance = new BrigadierDispatcher(plugin);
	}
	
	public static synchronized BrigadierDispatcher getInstance() {
		return instance;
	}
	
	
	
	
	
	private CommandDispatcher<CommandSender> dispatcher;
	/* package */ Plugin plugin;
	
	private BrigadierDispatcher(Plugin pl) {
		plugin = pl;
		dispatcher = new CommandDispatcher<>();
		ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
	}
	
	
	
	/* package */ LiteralCommandNode<CommandSender> register(LiteralArgumentBuilder<CommandSender> node) {
		return dispatcher.register(node);
	}
	
	
	public CommandDispatcher<CommandSender> getDispatcher() {
		return dispatcher;
	}
	
	
	
	@EventHandler
	public void onCommandsDeclare(CommandsDeclareEvent event) {
		dispatcher.getRoot().getChildren().forEach(node -> {
			event.getRoot().getChildren().remove(event.getRoot().getChild(node.getName())); // may not work in the future
			event.getRoot().addChild(node);
		});
	}
	
	
	
	@EventHandler
	public void onTabComplete(TabCompleteRequestEvent event) {
		if (!event.getCursor().startsWith("/"))
			return;
		
		String commandLine = event.getCursor().substring(1);
		
		String commandName = commandLine.split(" ", -1)[0];
		
		if (dispatcher.getRoot().getChild(commandName) == null)
			return;
		
		Suggestions suggestions = getSuggestions((ProxiedPlayer) event.getSender(), commandLine);
		
		// shift suggestion range 1 to the right to consider the /
		suggestions = new Suggestions(new StringRange(suggestions.getRange().getStart() + 1, suggestions.getRange().getEnd() + 1), suggestions.getList());
		
		event.setSuggestions(suggestions);
	}
	
	
	
	@EventHandler
	public void onChat(ChatEvent event) {
		if (!event.getMessage().startsWith("/"))
			return;
		
		String commandLine = event.getMessage().substring(1);
		
		String commandName = commandLine.split(" ", -1)[0];
		
		if (dispatcher.getRoot().getChild(commandName) == null)
			return;
		
		event.setCancelled(true);
		
		ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
			execute((ProxiedPlayer) event.getSender(), commandLine);
		});
		
	}
	
	
	
	
	/* package */ int execute(CommandSender sender, String commandWithoutSlash) {
		ParseResults<CommandSender> parsed = dispatcher.parse(commandWithoutSlash, sender);
		
		try {
			return dispatcher.execute(parsed);
		} catch (CommandSyntaxException e) {
			sender.sendMessage(Chat.failureText("Erreur d'utilisation de la commande : " + e.getMessage()).get());
			return 0;
		} catch (Throwable e) {
			sender.sendMessage(Chat.failureText("Erreur lors de l'exécution de la commande : " + e.getMessage()).get());
			Log.severe(e);
			return 0;
		}
		
	}
	
	/* package */ Suggestions getSuggestions(CommandSender sender, String buffer) {
		ParseResults<CommandSender> parsed = dispatcher.parse(buffer, sender);
		try {
			CompletableFuture<Suggestions> futureSuggestions = buildSuggestionBrigadier(parsed);
			return futureSuggestions.join();
		} catch (Throwable e) {
			sender.sendMessage(Chat.failureText("Erreur d’exécution des suggestions :\n" + e.getMessage()).get());
			Log.severe(e);
			return Suggestions.empty().join();
		}
	}



	CompletableFuture<Suggestions> buildSuggestionBrigadier(ParseResults<CommandSender> parsed) {
		int cursor = parsed.getReader().getTotalLength();
		final CommandContextBuilder<CommandSender> context = parsed.getContext();
		
		final SuggestionContext<CommandSender> nodeBeforeCursor = context.findSuggestionContext(cursor);
		final CommandNode<CommandSender> parent = nodeBeforeCursor.parent;
		final int start = Math.min(nodeBeforeCursor.startPos, cursor);
		
		final String fullInput = parsed.getReader().getString();
		final String truncatedInput = fullInput.substring(0, cursor);
		@SuppressWarnings("unchecked") final CompletableFuture<Suggestions>[] futures = new CompletableFuture[parent.getChildren().size()];
		int i = 0;
		for (final CommandNode<CommandSender> node : parent.getChildren()) {
		    CompletableFuture<Suggestions> future = Suggestions.empty();
		    try {
		        future = node.listSuggestions(context.build(truncatedInput), new SuggestionsBuilder(truncatedInput, start));
		    } catch (final CommandSyntaxException ignored) {
		    }
		    futures[i++] = future;
		}
		
		final CompletableFuture<Suggestions> result = new CompletableFuture<>();
		CompletableFuture.allOf(futures).thenRun(() -> {
		    final List<Suggestions> suggestions = new ArrayList<>();
		    for (final CompletableFuture<Suggestions> future : futures) {
		        suggestions.add(future.join());
		    }
		    result.complete(mergeSuggestionsOriginalOrdering(fullInput, suggestions));
		});
		return result;
	}
	
	// inspired from com.mojang.brigadier.suggestion.Suggestions#merge, but without the sorting part
	public static Suggestions mergeSuggestionsOriginalOrdering(String input, Collection<Suggestions> suggestions) {
		if (suggestions.isEmpty()) {
		    return new Suggestions(StringRange.at(0), new ArrayList<>(0));
		} else if (suggestions.size() == 1) {
		    return suggestions.iterator().next();
		}
		
		final List<Suggestion> texts = new ArrayList<>();
		for (final Suggestions suggestions1 : suggestions) {
		    texts.addAll(suggestions1.getList());
		}
		return createSuggestionsOriginalOrdering(input, texts);
	}
	
	// inspired from com.mojang.brigadier.suggestion.Suggestions#create, but without the sorting part
	public static Suggestions createSuggestionsOriginalOrdering(String command, Collection<Suggestion> suggestions) {
		if (suggestions.isEmpty()) {
			return new Suggestions(StringRange.at(0), new ArrayList<>(0));
		}
		int start = Integer.MAX_VALUE;
		int end = Integer.MIN_VALUE;
		for (final Suggestion suggestion : suggestions) {
		    start = Math.min(suggestion.getRange().getStart(), start);
		    end = Math.max(suggestion.getRange().getEnd(), end);
		}
		final StringRange range = new StringRange(start, end);
		final List<Suggestion> texts = new ArrayList<>(suggestions.size());
		for (final Suggestion suggestion : suggestions) {
		    texts.add(suggestion.expand(command, range));
		}
		return new Suggestions(range, texts);
	}

}
