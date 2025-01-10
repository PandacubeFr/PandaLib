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

/**
 * Fired at the beginning of the server stop process.
 * More specifically, this event is called when the first plugin is disabling ({@link PluginDisableEvent}) while
 * {@link Bukkit#isStopping()} returns true.
 * <p>
 * This event can be useful when a plugin want to execute stuff on server stop as soon as possible in the process,
 * but not when the plugin itself is disabling (because some part of the Bukkit API is not usable at that moment).
 */
public class ServerStopEvent extends ServerEvent {


    private static final HandlerList handlers = new HandlerList();

    /**
     * Gets the handler list of the event.
     * @return the handler list of the event.
     */
    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    private static boolean hasTriggered = false;
    private static boolean isInit = false;

    /**
     * Register the event used to detect the server stop.
     */
    public static void init() {
        if (isInit)
            return;

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

        isInit = true;
    }





    private ServerStopEvent() {}


    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }





}
