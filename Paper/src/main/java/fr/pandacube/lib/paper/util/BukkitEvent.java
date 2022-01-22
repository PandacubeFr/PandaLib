package fr.pandacube.lib.paper.util;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.scheduler.BukkitTask;

import fr.pandacube.lib.core.util.Reflect;
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
		List<Class<? extends Event>> classes = Reflect.getAllSubclasses(Event.class);
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
	
	/**
	 * Abstract implementation of {@link EventListener} that ensure as best as it can,
	 * that it is the last listener called to handle the event.
	 *
	 * @param <E> the type of the event
	 */
	public static abstract class EnforcedLastListener<E extends Event> implements EventListener<E> {
		private final Class<E> eventClass;
		private final boolean ignoreCancelled;
		
		public EnforcedLastListener(Class<E> eventClass, boolean ignoreCancelled) {
			this.eventClass = eventClass;
			this.ignoreCancelled = ignoreCancelled;
			register();
		}
		
		private void register() {
			BukkitEvent.register(eventClass, this, EventPriority.MONITOR, ignoreCancelled);
		}
		
		@Override
		public void execute(Listener var1, Event var2) throws EventException {
			EventListener.super.execute(var1, var2);
			checkIfListenerIsLast();
		}
		
		
		private AtomicReference<BukkitTask> listenerCheckTask = new AtomicReference<>();
		
		private void checkIfListenerIsLast() {
			synchronized (listenerCheckTask) {
				if (listenerCheckTask.get() != null)
					return;
				RegisteredListener[] listeners = BukkitEvent.getHandlerList(eventClass).getRegisteredListeners();
				if (listeners[listeners.length - 1].getListener() != this) {
					listenerCheckTask.set(Bukkit.getScheduler().runTask(PandaLibPaper.getPlugin(), () -> {
						// need to re-register the event so we are last
						BukkitEvent.unregister(this);
						register();
						listenerCheckTask.set(null);
					}));
				}
			}
		}
	}
	

}
