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

public abstract class BrigadierDispatcher<S> {


    private final CommandDispatcher<S> dispatcher = new CommandDispatcher<>();



    public void register(LiteralCommandNode<S> node) {
        dispatcher.getRoot().addChild(node);
    }




    public CommandDispatcher<S> getDispatcher() {
        return dispatcher;
    }





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






    protected abstract void sendSenderMessage(S sender, ComponentLike message);

}
