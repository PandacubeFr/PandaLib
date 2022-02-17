package fr.pandacube.lib.paper;

import org.bukkit.plugin.Plugin;

import fr.pandacube.lib.paper.reflect.ReflectRegistry;

public class PandaLibPaper {
	
	private static Plugin plugin;
	
	public static void init(Plugin plugin) {
		PandaLibPaper.plugin = plugin;
		
		ReflectRegistry.init();
		
		
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}

}
