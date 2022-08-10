package fr.pandacube.lib.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.util.Log;
import net.kyori.adventure.text.ComponentLike;

import java.util.concurrent.CompletableFuture;

/**
 * Abstract class that holds a Brigadier {@link CommandDispatcher} instance.
 * Subclasses contains logic to integrate this commands dispatcher into their environment (like Bungee or CLI app).
 * @param <S> the command source (or command sender) type.
 */
public abstract class BrigadierDispatcher<S> {


    private final CommandDispatcher<S> dispatcher = new CommandDispatcher<>();


    /**
     * Registers the provided command node into this dispatcher.
     * @param node the node to register.
     */
    public void register(LiteralCommandNode<S> node) {
        dispatcher.getRoot().addChild(node);
    }


    /**
     * Returns the Brigadier dispatcher.
     * @return the Brigadier dispatcher.
     */
    public CommandDispatcher<S> getDispatcher() {
        return dispatcher;
    }


    /**
     * Executes the provided command as the provided sender.
     * @param sender the command sender.
     * @param commandWithoutSlash the command, without the eventual slash at the begining.
     * @return the value returned by the executed command.
     */
    public int execute(S sender, String commandWithoutSlash) {
        ParseResults<S> parsed = dispatcher.parse(commandWithoutSlash, sender);

        try {
            return dispatcher.execute(parsed);
        } catch (CommandSyntaxException e) {
            sendSenderMessage(sender, Chat.failureText("Error while using the command: " + e.getMessage()));
            return 0;
        } catch (Throwable e) {
            sendSenderMessage(sender, Chat.failureText("Error while running the command: " + e.getMessage()));
            Log.severe(e);
            return 0;
        }
    }


    /**
     * Gets the suggestions for the currenlty being typed command.
     * @param sender the command sender.
     * @param buffer the command that is being typed.
     * @return the suggestions for the currenlty being typed command.
     */
    public Suggestions getSuggestions(S sender, String buffer) {
        ParseResults<S> parsed = dispatcher.parse(buffer, sender);
        try {
            CompletableFuture<Suggestions> futureSuggestions = BrigadierSuggestionsUtil.buildSuggestionBrigadier(parsed);
            return futureSuggestions.join();
        } catch (Throwable e) {
            sendSenderMessage(sender, Chat.failureText("Error while generating the suggestions:\n" + e.getMessage()));
            Log.severe(e);
            return Suggestions.empty().join();
        }
    }


    /**
     * Sends the provided message to the sender.
     * @param sender the sender to send the message to.
     * @param message the message to send.
     */
    protected abstract void sendSenderMessage(S sender, ComponentLike message);

}
