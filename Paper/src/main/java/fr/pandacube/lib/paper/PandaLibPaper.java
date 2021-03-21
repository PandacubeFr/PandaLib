package fr.pandacube.lib.paper;

import org.bukkit.plugin.Plugin;

public class PandaLibPaper {
	
	private static Plugin plugin;
	
	public static void init(Plugin plugin) {
		PandaLibPaper.plugin = plugin;
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}

}
