package fr.pandacube.lib.paper;

import fr.pandacube.lib.paper.modules.PerformanceAnalysisManager;
import org.bukkit.plugin.Plugin;

public class PandaLibPaper {
	
	private static Plugin plugin;
	
	public static void init(Plugin plugin) {
		PandaLibPaper.plugin = plugin;

		PerformanceAnalysisManager.getInstance(); // initialize
		
	}


	public static void disable() {
		PerformanceAnalysisManager.getInstance().cancelInternalBossBar();
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}

}
