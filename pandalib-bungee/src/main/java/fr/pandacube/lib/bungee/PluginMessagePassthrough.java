package fr.pandacube.lib.bungee;

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

/**
 * Utility class for Bungee plugins that pass the requested plugin message channels through the bungeecord instance.
 * By default, plugin messages that are not registered in BungeeCord are dropped.
 * <p>
 * Usage example, in your plugin init code:
 * <pre>{@code
 * PluginMessagePassthrough.init(yourPluginInstance);
 * PluginMessagePassthrough.register("worldedit:cui"); // plugin message used by WorldEdit
 * }</pre>
 */
public class PluginMessagePassthrough implements Listener {
    private static final List<String> channels = Collections.synchronizedList(new ArrayList<>());
    private static final PluginMessagePassthrough instance = new PluginMessagePassthrough();

    /**
     * Initialize the {@link PluginMessagePassthrough}.
     * It registers the required event listener.
     * @param plugin the plugin instance.
     */
    public static void init(Plugin plugin) {
        PluginManager pm = ProxyServer.getInstance().getPluginManager();
        pm.unregisterListener(instance);
        pm.registerListener(plugin, instance);
    }

    /**
     * Clears the list of passed through plugin message channels.
     */
    public static void clear() {
        synchronized (channels) {
            unregisterAll(channels.toArray(new String[0]));
        }
    }

    /**
     * Adds all the provided plugin message channels to pass through.
     * @param channels the channels to register.
     */
    public static void registerAll(String... channels) {
        for (String channel : channels)
            register(channel);
    }

    /**
     * Removes all the provided plugin message channels from pass through.
     * @param channels the channels to unregister.
     */
    public static void unregisterAll(String... channels) {
        for (String channel : channels)
            unregister(channel);
    }

    /**
     * Adds the provided plugin message to pass through.
     * @param channel the channel to register.
     */
    public static void register(String channel) {
        synchronized (channels) {
            if (channels.contains(channel))
                return;
            ProxyServer.getInstance().registerChannel(channel);
            channels.add(channel);
        }
    }

    /**
     * Removes the provided plugin message channel from pass through.
     * @param channel the channel to unregister.
     */
    public static void unregister(String channel) {
        synchronized (channels) {
            if (!channels.contains(channel))
                return;
            ProxyServer.getInstance().unregisterChannel(channel);
            channels.remove(channel);
        }
    }


    private PluginMessagePassthrough() { }


    /**
     * Event handler for {@link PluginMessageEvent}.
     * @param event the event.
     */
    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        String channel = event.getTag();
        if (!channels.contains(channel))
            return;
        if (event.getReceiver() instanceof ProxiedPlayer pp) {
            pp.sendData(channel, event.getData());
        } else if (event.getReceiver() instanceof Server sv) {
            sv.sendData(channel, event.getData());
        }
    }
}
