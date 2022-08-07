package fr.pandacube.lib.commands;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Abstract class that holds the logic of a specific command to be integrated in a Brigadier command dispatcher.
 * Subclasses may use any mechanism to integrate this command in the environment’s Brigadier instance, during the
 * instantiation of this object.
 * @param <S> the command source (or command sender) type.
 */
public abstract class BrigadierCommand<S> {






    protected abstract LiteralArgumentBuilder<S> buildCommand();

    /**
     * Method to implement if the reference to the command node has to be known when building the subcommands.
     * @param commandNode the command node builded from {@link #buildCommand()}.
     */
    protected void postBuildCommand(LiteralCommandNode<S> commandNode) {
        // default implementation does nothing.
    }

    protected String[] getAliases() {
        return new String[0];
    }














    public LiteralArgumentBuilder<S> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }
    public <T> RequiredArgumentBuilder<S, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }








    public abstract boolean isPlayer(S sender);

    public abstract boolean isConsole(S sender);

    public Predicate<S> isPlayer() {
        return this::isPlayer;
    }

    public Predicate<S> isConsole() {
        return this::isConsole;
    }

    public abstract Predicate<S> hasPermission(String permission);









    public static boolean isLiteralParsed(CommandContext<?> context, String literal) {
        for (ParsedCommandNode<?> node : context.getNodes()) {
            if (node.getNode() instanceof LiteralCommandNode<?> lNode
                    && lNode.getLiteral().equals(literal))
                return true;
        }
        return false;
    }








    public static <T> T tryGetArgument(CommandContext<?> context, String argument, Class<T> type) {
        return tryGetArgument(context, argument, type, Function.identity(), null);
    }

    public static <ST, T> T tryGetArgument(CommandContext<?> context, String argument, Class<ST> sourceType, Function<ST, T> transformIfFound) {
        return tryGetArgument(context, argument, sourceType, transformIfFound, null);
    }

    public static <T> T tryGetArgument(CommandContext<?> context, String argument, Class<T> type, T deflt) {
        return tryGetArgument(context, argument, type, Function.identity(), deflt);
    }

    public static <ST, T> T tryGetArgument(CommandContext<?> context, String argument, Class<ST> sourceType, Function<ST, T> transformIfFound, T deflt) {
        ST sourceValue;
        try {
            sourceValue = context.getArgument(argument, sourceType);
        } catch (IllegalArgumentException e) {
            return deflt;
        }
        return transformIfFound.apply(sourceValue);
    }










    public static CommandSyntaxException newCommandException(String message) {
        return new SimpleCommandExceptionType(new LiteralMessage(message)).create();
    }








    protected <AS> SuggestionProvider<S> wrapSuggestions(SuggestionsSupplier<AS> suggestions, Function<S, AS> senderUnwrapper) {
        return (context, builder) -> {
            AS sender = senderUnwrapper.apply(context.getSource());
            String message = builder.getInput();
            try {
                int tokenStartPos = builder.getStart();

                int firstSpacePos = message.indexOf(" ");
                String[] args = (firstSpacePos + 1 > tokenStartPos - 1) ? new String[0]
                        : message.substring(firstSpacePos + 1, tokenStartPos - 1).split(" ", -1);
                args = Arrays.copyOf(args, args.length + 1);
                args[args.length - 1] = message.substring(tokenStartPos);

                for (String s : suggestions.getSuggestions(sender, args.length - 1, args[args.length - 1], args)) {
                    if (s != null)
                        builder.suggest(s);
                }
            } catch (Throwable e) {
                Log.severe("Error while tab-completing '" + message + "' for " + sender, e);
            }
            return BrigadierSuggestionsUtil.completableFutureSuggestionsKeepsOriginalOrdering(builder);
        };
    }

}
