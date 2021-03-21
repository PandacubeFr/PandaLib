package fr.pandacube.lib.paper.util;

import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.IllegalPluginAccessException;

import fr.pandacube.lib.core.util.ReflexionUtil;
import fr.pandacube.lib.paper.PandaLibPaper;

public class BukkitEvent {
	
	public static <E extends Event> Listener register(Class<E> eventClass, EventListener<E> eventExecutor) {
		return register(eventClass, eventExecutor, EventPriority.NORMAL, false);
	}
	
	public static <E extends Event> Listener register(Class<E> eventClass, EventListener<E> eventExecutor, EventPriority priority) {
		return register(eventClass, eventExecutor, priority, false);
	}
	
	public static <E extends Event> Listener register(Class<E> eventClass, EventListener<E> eventExecutor, boolean ignoreCancelled) {
		return register(eventClass, eventExecutor, EventPriority.NORMAL, ignoreCancelled);
	}
	
	public static <E extends Event> Listener register(Class<E> eventClass, EventListener<E> eventExecutor, EventPriority priority, boolean ignoreCancelled) {
		Bukkit.getPluginManager().registerEvent(eventClass, eventExecutor, priority, eventExecutor, PandaLibPaper.getPlugin(), ignoreCancelled);
		return eventExecutor;
	}
	
	public static void register(Listener l) {
		Bukkit.getPluginManager().registerEvents(l, PandaLibPaper.getPlugin());
	}
	
	
	
	public static void unregister(Listener listener) {
		HandlerList.unregisterAll(listener);
	}
	
	

	public static List<Class<? extends Event>> getAllEventClasses() {
		List<Class<? extends Event>> classes = ReflexionUtil.getAllSubclasses(Event.class);
		classes.removeIf(e -> getHandlerList(e) == null);
		return classes;
	}
	
	
	
    // method retrieved from OB.plugin.SimplePluginManager#getEventListeners
    public static HandlerList getHandlerList(Class<? extends Event> type) throws IllegalPluginAccessException {
        try {
            Method method = getRegistrationClass(type).getDeclaredMethod("getHandlerList", new Class[0]);
            method.setAccessible(true);
            return (HandlerList)method.invoke(null, new Object[0]);
        }
        catch (Exception e) {
            return null;
        }
    }

    // method retrieved from OB.plugin.SimplePluginManager
    private static Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) throws IllegalPluginAccessException {
        try {
            clazz.getDeclaredMethod("getHandlerList", new Class[0]);
            return clazz;
        }
        catch (NoSuchMethodException e) {	
            if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Event.class) && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            }
            return null;
        }
    }
	
	
	
	public interface EventListener<E extends Event> extends Listener, EventExecutor {
		
		public abstract void onEvent(E event);
		
		@SuppressWarnings("unchecked")
		@Override
		default void execute(Listener var1, Event var2) throws EventException {
			onEvent((E)var2);
		}
	}
	

}
