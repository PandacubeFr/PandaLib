package fr.pandacube.lib.cli;

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

import fr.pandacube.lib.core.util.Log;
import jline.console.completer.Completer;

public class BrigadierDispatcher implements Completer {
	
	public static final BrigadierDispatcher instance = new BrigadierDispatcher();
	
	
	
	
	
	
	
	private CommandDispatcher<Object> dispatcher;
	
	private Object sender = new Object();	
	
	public BrigadierDispatcher() {
		dispatcher = new CommandDispatcher<>();
	}
	
	
	
	/* package */ LiteralCommandNode<Object> register(LiteralArgumentBuilder<Object> node) {
		return dispatcher.register(node);
	}
	
	
	public int execute(String commandWithoutSlash) {
		ParseResults<Object> parsed = dispatcher.parse(commandWithoutSlash, sender);
		
		try {
			return dispatcher.execute(parsed);
		} catch (CommandSyntaxException e) {
			Log.severe("Erreur d’utilisation de la commande : " + e.getMessage());
			return 0;
		} catch (Throwable e) {
			Log.severe("Erreur lors de l’exécution de la commande : ", e);
			return 0;
		}
		
	}
	
	
	@Override
	public int complete(String buffer, int cursor, List<CharSequence> candidates) {
		
		String bufferBeforeCursor = buffer.substring(0, cursor);
		
		Suggestions completeResult = getSuggestions(bufferBeforeCursor);
		
		completeResult.getList().stream().map(s -> s.getText()).forEach(candidates::add);
		
		return completeResult.getRange().getStart();
	}
	
	/* package */ Suggestions getSuggestions(String buffer) {
		ParseResults<Object> parsed = dispatcher.parse(buffer, sender);
		try {
			CompletableFuture<Suggestions> futureSuggestions = buildSuggestionBrigadier(parsed);
			return futureSuggestions.join();
		} catch (Throwable e) {
			Log.severe("Erreur d’exécution des suggestions :\n" + e.getMessage(), e);
			return Suggestions.empty().join();
		}
	}



	CompletableFuture<Suggestions> buildSuggestionBrigadier(ParseResults<Object> parsed) {
		int cursor = parsed.getReader().getTotalLength();
		final CommandContextBuilder<Object> context = parsed.getContext();
		
		final SuggestionContext<Object> nodeBeforeCursor = context.findSuggestionContext(cursor);
		final CommandNode<Object> parent = nodeBeforeCursor.parent;
		final int start = Math.min(nodeBeforeCursor.startPos, cursor);
		
		final String fullInput = parsed.getReader().getString();
		final String truncatedInput = fullInput.substring(0, cursor);
		@SuppressWarnings("unchecked") final CompletableFuture<Suggestions>[] futures = new CompletableFuture[parent.getChildren().size()];
		int i = 0;
		for (final CommandNode<Object> node : parent.getChildren()) {
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
