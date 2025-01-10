package fr.pandacube.lib.paper;

import fr.pandacube.lib.paper.event.ServerStopEvent;
import fr.pandacube.lib.paper.json.PaperJson;
import fr.pandacube.lib.paper.modules.PerformanceAnalysisManager;
import org.bukkit.plugin.Plugin;

/**
 * Main class for pandalib-paper.
 */
public class PandaLibPaper {
	
	private static Plugin plugin;

	/**
	 * Method to call in plugin's {@link Plugin#onLoad()} method.
	 * @param plugin the plugin instance.
	 */
	public static void onLoad(Plugin plugin) {
		PandaLibPaper.plugin = plugin;
		PaperJson.init();
	}

	/**
	 * Method to call in plugin's {@link Plugin#onEnable()} method.
	 */
	public static void onEnable() {
		PerformanceAnalysisManager.getInstance(); // initialize
		ServerStopEvent.init();
	}



	/**
	 * Method to call in plugin's {@link Plugin#onDisable()} method.
	 */
	public static void disable() {
		PerformanceAnalysisManager.getInstance().deinit();
	}

	/**
	 * Gets the plugin instance.
	 * @return the plugin instance provided with {@link #onLoad(Plugin)}.
	 */
	public static Plugin getPlugin() {
		return plugin;
	}

	private PandaLibPaper() {}

}
