package fr.pandacube.lib.commands;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import fr.pandacube.lib.reflect.Reflect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Utility methods to replace some functionalities of Brigadier, especialy suggestion sorting that we don’t like.
 */
public class BrigadierSuggestionsUtil {



    public static <S> CompletableFuture<Suggestions> buildSuggestionBrigadier(ParseResults<S> parsed) {
        int cursor = parsed.getReader().getTotalLength();
        final CommandContextBuilder<S> context = parsed.getContext();

        final SuggestionContext<S> nodeBeforeCursor = context.findSuggestionContext(cursor);
        final CommandNode<S> parent = nodeBeforeCursor.parent;
        final int start = Math.min(nodeBeforeCursor.startPos, cursor);

        final String fullInput = parsed.getReader().getString();
        final String truncatedInput = fullInput.substring(0, cursor);
        @SuppressWarnings("unchecked") final CompletableFuture<Suggestions>[] futures = new CompletableFuture[parent.getChildren().size()];
        int i = 0;
        for (final CommandNode<S> node : parent.getChildren()) {
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





    public static CompletableFuture<Suggestions> completableFutureSuggestionsKeepsOriginalOrdering(SuggestionsBuilder builder) {
        return CompletableFuture.completedFuture(
                createSuggestionsOriginalOrdering(builder.getInput(), getSuggestionsFromSuggestionsBuilder(builder))
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
