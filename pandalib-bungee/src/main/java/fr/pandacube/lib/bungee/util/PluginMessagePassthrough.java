package fr.pandacube.lib.bungee.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.event.EventHandler;

public class PluginMessagePassthrough implements Listener {
	private static final List<String> channels = Collections.synchronizedList(new ArrayList<>());
	private static final PluginMessagePassthrough instance = new PluginMessagePassthrough();
	
	public static void init(Plugin plugin) {
		PluginManager pm = ProxyServer.getInstance().getPluginManager();
		pm.unregisterListener(instance);
		pm.registerListener(plugin, instance);
	}
	
	public static void clear() {
		synchronized (channels) {
			unregisterAll(channels.toArray(new String[0]));
		}
	}
	
	public static void registerAll(String... channels) {
		for (String channel : channels)
			register(channel);
	}
	
	public static void unregisterAll(String... channels) {
		for (String channel : channels)
			unregister(channel);
	}
	
	public static void register(String channel) {
		synchronized (channels) {
			if (channels.contains(channel))
				return;
			ProxyServer.getInstance().registerChannel(channel);
			channels.add(channel);
		}
	}
	
	public static void unregister(String channel) {
		synchronized (channels) {
			if (!channels.contains(channel))
				return;
			ProxyServer.getInstance().unregisterChannel(channel);
			channels.remove(channel);
		}
	}
	
	
	@EventHandler
	public void onPluginMessage(PluginMessageEvent event) {
		String channel = event.getTag();
		if (!channels.contains(channel))
			return;
		if (event.getReceiver() instanceof ProxiedPlayer) {
			((ProxiedPlayer)event.getReceiver()).sendData(channel, event.getData());
		} else if (event.getReceiver() instanceof Server) {
			((Server)event.getReceiver()).sendData(channel, event.getData());
		}
	}
}
