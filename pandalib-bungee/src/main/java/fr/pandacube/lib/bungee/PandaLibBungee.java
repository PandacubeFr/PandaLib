package fr.pandacube.lib.bungee;

import fr.pandacube.lib.bungee.util.BungeeDailyLogRotateFileHandler;
import fr.pandacube.lib.bungee.util.PluginMessagePassThrough;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * General class used to initialize some tools of pandalib-bungee, following the bungee plugin's lifecycle.
 */
public class PandaLibBungee {

    private static Plugin plugin;

    /**
     * Method to be called in {@link Plugin#onLoad()} method.
     * @param plugin the plugin instance.
     */
    public static void onLoad(Plugin plugin) {
        PandaLibBungee.plugin = plugin;

    }

    /**
     * Method to be called in {@link Plugin#onEnable()} method.
     */
    public static void onEnable() {
        PluginMessagePassThrough.init(plugin);
        BungeeDailyLogRotateFileHandler.init(true);
    }


    /**
     * Method to be called in {@link Plugin#onDisable()} method.
     */
    public static void disable() {

    }

    /**
     * Returns the plugin instance.
     * @return the plugin instance.
     */
    public static Plugin getPlugin() {
        return plugin;
    }

    private PandaLibBungee() {}

}
