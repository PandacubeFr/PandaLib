package fr.pandacube.lib.bungee.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.commands.BrigadierCommand;
import fr.pandacube.lib.commands.BrigadierDispatcher;
import fr.pandacube.lib.commands.BrigadierSuggestionsUtil;
import fr.pandacube.lib.util.Log;
import net.kyori.adventure.text.ComponentLike;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BungeeBrigadierDispatcher extends BrigadierDispatcher<CommandSender> implements Listener {




	/* package */ final Plugin plugin;

	public BungeeBrigadierDispatcher(Plugin pl) {
		plugin = pl;
		ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
	}
	

	
	
	
	@EventHandler
	public void onChat(ChatEvent event) {
		if (!event.getMessage().startsWith("/"))
			return;
		
		String commandLine = event.getMessage().substring(1);
		
		String commandName = commandLine.split(" ", -1)[0];
		
		if (getDispatcher().getRoot().getChild(commandName) == null)
			return;
		
		event.setCancelled(true);
		
		ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> execute((ProxiedPlayer) event.getSender(), commandLine));
		
	}


	@Override
	protected void sendSenderMessage(CommandSender sender, ComponentLike message) {
		sender.sendMessage(Chat.toBungee(message.asComponent()));
	}
}
