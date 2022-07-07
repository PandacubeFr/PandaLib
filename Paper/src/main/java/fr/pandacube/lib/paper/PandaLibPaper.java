package fr.pandacube.lib.paper;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.WrapperRegistry;
import org.bukkit.plugin.Plugin;

public class PandaLibPaper {
	
	private static Plugin plugin;
	
	public static void init(Plugin plugin) {
		PandaLibPaper.plugin = plugin;

		NMSReflect.init();
		WrapperRegistry.init();
		
		
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}

}
