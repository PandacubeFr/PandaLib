package fr.pandacube.lib.bungee.commands;

import fr.pandacube.lib.bungee.PandaLibBungee;
import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.commands.BrigadierDispatcher;
import net.kyori.adventure.text.ComponentLike;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

/**
 * Implementation of {@link BrigadierDispatcher} that integrates the commands into BungeeCord API, so the players and
 * the console can actually execute them.
 */
public class BungeeBrigadierDispatcher extends BrigadierDispatcher<CommandSender> implements Listener {


	private static BungeeBrigadierDispatcher instance = null;

	public static synchronized BungeeBrigadierDispatcher getInstance() {
		return instance;
	}




	/* package */ final Plugin plugin;

	/**
	 * Create a new instance of {@link BungeeBrigadierDispatcher}.
	 * @param pl the plugin that creates this dispatcher.
	 */
	public BungeeBrigadierDispatcher(Plugin pl) {
		if (instance != null)
			throw new IllegalStateException("Cannot instanciante more than one BungeeBrigadierDispatcher");
		instance = this;
		plugin = pl;
		ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
	}


	/**
	 * Called when a player sends a chat message. Used to gets the typed command and execute it.
	 * @param event the event.
	 */
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
