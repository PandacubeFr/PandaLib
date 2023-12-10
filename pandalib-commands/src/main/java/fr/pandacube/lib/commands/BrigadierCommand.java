package fr.pandacube.lib.commands;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.pandacube.lib.util.log.Log;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Abstract class that holds the logic of a specific command to be integrated in a Brigadier command dispatcher.
 * Subclasses may use any mechanism to integrate this command in the environment’s Brigadier instance (or in a
 * {@link BrigadierDispatcher} instance), during the instantiation of this object.
 * @param <S> the command source (or command sender) type.
 */
public abstract class BrigadierCommand<S> {


    /**
     * Returns a builder for this command.
     * Concrete class should include any element in the builder that is needed to build the command (sub-commands and
     * arguments, requirements, redirection, ...).
     * If any of the sub-commands and arguments needs to know the {@link LiteralCommandNode} built from the returned
     * {@link LiteralArgumentBuilder}, this can be done by overriding {@link #postBuildCommand(LiteralCommandNode)}.
     * @return a builder for this command.
     */
    protected abstract LiteralArgumentBuilder<S> buildCommand();

    /**
     * Method to override if the reference to the command node has to be known when building the subcommands.
     * @param commandNode the command node built from {@link #buildCommand()}.
     */
    protected void postBuildCommand(LiteralCommandNode<S> commandNode) {
        // default implementation does nothing.
    }

    /**
     * Method to override if this command has any aliases.
     * @return an array of string corresponding to the aliases. This must not include the orignal command name (that
     * is the name of the literal command node built from {@link #buildCommand()}).
     */
    protected String[] getAliases() {
        return new String[0];
    }


    /**
     * Creates a new {@link LiteralArgumentBuilder} that has the provided name.
     * @param name the name of the command node.
     * @return a new {@link LiteralArgumentBuilder} that has the provided name.
     */
    public LiteralArgumentBuilder<S> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    /**
     * Creates a new {@link RequiredArgumentBuilder} that has the provided name.
     * @param name the name of the command node.
     * @param type the type of the argument.
     * @param <T> the argument type.
     * @return a new {@link RequiredArgumentBuilder} that has the provided name.
     */
    public <T> RequiredArgumentBuilder<S, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }


    /**
     * Tells if the provided command sender is a player.
     * @param sender the sender to test if it’s a player or not.
     * @return true if the sender is a player, false otherwise.
     */
    public abstract boolean isPlayer(S sender);

    /**
     * Tells if the provided command sender is the console.
     * @param sender the sender to test if it’s the console or not.
     * @return true if the sender is the console, false otherwise.
     */
    public abstract boolean isConsole(S sender);

    /**
     * Provides a {@link Predicate} that tests if the command sender is a player.
     * @return a {@link Predicate} that tests if the command sender is a player.
     * @see #isPlayer(Object)
     */
    public Predicate<S> isPlayer() {
        return this::isPlayer;
    }

    /**
     * Provides a {@link Predicate} that tests if the command sender is the console.
     * @return a {@link Predicate} that tests if the command sender is the console.
     * @see #isConsole(Object)
     */
    public Predicate<S> isConsole() {
        return this::isConsole;
    }

    /**
     * Provides a {@link Predicate} that tests if the command sender has the provided permission.
     * @param permission the permission tested by the returned predicate on the command sender.
     * @return a {@link Predicate} that tests if the command sender has the provided permission.
     */
    public abstract Predicate<S> hasPermission(String permission);







    /**
     * Determines if the literal node is found in the command.
     * <p>
     * <b>Be aware that this method search even beyond any fork or redirection, so it may encounter literal nodes that
     * have the provided name but belong to other commands, so it can produce a false positive.</b>
     * @param context the context of the command execution.
     * @param literal the literal command node to search for in the typed command.
     * @return true if the provided literal is in the typed command, false otherwise.
     */
    public static boolean isLiteralParsed(CommandContext<?> context, String literal) {
        for (ParsedCommandNode<?> node : context.getNodes()) {
            if (node.getNode() instanceof LiteralCommandNode<?> lNode
                    && lNode.getLiteral().equals(literal))
                return true;
        }
        return false;
    }


    /**
     * Gets the argument value from the provided context, silently returning null (instead of throwing an exception)
     * if the argument is not found.
     * @param context the context of the command execution.
     * @param argument the argument to search for.
     * @param type the type of the argument.
     * @param <T> the argument type.
     * @return the value of the argument, or null if not found.
     */
    public static <T> T tryGetArgument(CommandContext<?> context, String argument, Class<T> type) {
        return tryGetArgument(context, argument, type, Function.identity(), null);
    }

    /**
     * Gets the argument value from the provided context, silently returning a default value (instead of throwing an
     * exception) if the argument is not found.
     * @param context the context of the command execution.
     * @param argument the argument to search for.
     * @param type the type of the argument.
     * @param deflt the default value if not found.
     * @param <T> the argument type.
     * @return the value of the argument, or {@code deflt} if not found.
     */
    public static <T> T tryGetArgument(CommandContext<?> context, String argument, Class<T> type, T deflt) {
        return tryGetArgument(context, argument, type, Function.identity(), deflt);
    }

    /**
     * Gets the argument value from the provided context and transform it using the provided function, or silently
     * returning null (instead of throwing an exception) if the argument is not found.
     * @param context the context of the command execution.
     * @param argument the argument to search for.
     * @param sourceType the type of the argument in the command context.
     * @param transformIfFound the function to transform the argument value before returning.
     * @param <ST> the argument type in the command context.
     * @param <T> the returned type.
     * @return the value of the argument, transformed by {@code transformIfFound}, or null if not found.
     */
    public static <ST, T> T tryGetArgument(CommandContext<?> context, String argument, Class<ST> sourceType, Function<ST, T> transformIfFound) {
        return tryGetArgument(context, argument, sourceType, transformIfFound, null);
    }

    /**
     * Gets the argument value from the provided context and transform it using the provided function, or silently
     * returning a default value (instead of throwing an exception) if the argument is not found.
     * @param context the context of the command execution.
     * @param argument the argument to search for.
     * @param sourceType the type of the argument in the command context.
     * @param transformIfFound the function to transform the argument value before returning.
     * @param deflt the default value if not found.
     * @param <ST> the argument type in the command context.
     * @param <T> the returned type.
     * @return the value of the argument, transformed by {@code transformIfFound}, or {@code deflt} if not found.
     */
    public static <ST, T> T tryGetArgument(CommandContext<?> context, String argument, Class<ST> sourceType, Function<ST, T> transformIfFound, T deflt) {
        ST sourceValue;
        try {
            sourceValue = context.getArgument(argument, sourceType);
        } catch (IllegalArgumentException e) {
            return deflt;
        }
        return transformIfFound.apply(sourceValue);
    }


    /**
     * Creates a new instance of {@link CommandSyntaxException} with the provided message.
     * @param message the exception message.
     * @return a new instance of {@link CommandSyntaxException} with the provided message.
     */
    public static CommandSyntaxException newCommandException(String message) {
        return new SimpleCommandExceptionType(new LiteralMessage(message)).create();
    }



    /**
     * Wraps the provided {@link SuggestionsSupplier} into a Brigadier’s {@link SuggestionProvider}.
     * @param suggestions the suggestions to wrap.
     * @param senderUnwrapper function to convert the command sender provided by brigadier into the command sender
     *                        supported by {@link SuggestionsSupplier}.
     * @return a {@link SuggestionProvider} generating the suggestions from the provided {@link SuggestionsSupplier}.
     * @param <AS> the type of command sender supported by the {@link SuggestionsSupplier}.
     */
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

                List<String> wrappedResult = suggestions.getSuggestions(sender, args.length - 1, args[args.length - 1], args);
                if (wrappedResult != null) {
                    for (String s : wrappedResult) {
                        if (s != null)
                            builder.suggest(s);
                    }
                }
            } catch (Throwable e) {
                Log.severe("Error while tab-completing '" + message + "' for " + sender, e);
            }
            return BrigadierSuggestionsUtil.completableFutureSuggestionsKeepsOriginalOrdering(builder);
        };
    }

}
