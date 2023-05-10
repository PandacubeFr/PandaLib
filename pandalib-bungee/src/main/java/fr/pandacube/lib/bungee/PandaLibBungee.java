package fr.pandacube.lib.bungee;

import fr.pandacube.lib.bungee.util.BungeeDailyLogRotateFileHandler;
import fr.pandacube.lib.bungee.util.PluginMessagePassthrough;
import net.md_5.bungee.api.plugin.Plugin;

public class PandaLibBungee {

    private static Plugin plugin;

    public static void onLoad(Plugin plugin) {
        PandaLibBungee.plugin = plugin;

    }

    public static void onEnable() {
        PluginMessagePassthrough.init(plugin);
        BungeeDailyLogRotateFileHandler.init(true);
    }


    public static void disable() {

    }

    public static Plugin getPlugin() {
        return plugin;
    }
}
