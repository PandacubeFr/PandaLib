package fr.pandacube.lib.paper.event;

import fr.pandacube.lib.paper.util.BukkitEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.ServerEvent;
import org.jetbrains.annotations.NotNull;

public class ServerStopEvent extends ServerEvent {


    private static final HandlerList handlers = new HandlerList();

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }




    private static boolean hasTriggered = false;

    public static void init() {
        BukkitEvent.register(new Listener() {

            @EventHandler(priority = EventPriority.LOWEST)
            public void onPluginDisable(PluginDisableEvent event) {
                if (!Bukkit.isStopping())
                    return;
                if (hasTriggered)
                    return;
                hasTriggered = true;
                new ServerStopEvent().callEvent();
            }

        });
    }


}
