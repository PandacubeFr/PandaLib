package fr.pandacube.lib.paper.util;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.scheduler.BukkitTask;

import fr.pandacube.lib.paper.PandaLibPaper;
import fr.pandacube.lib.reflect.Reflect;

/**
 * Utility class to more concisely handle Bukkit Events
 */
public class BukkitEvent {

	/**
	 * Register a single event executor.
	 * <p>
	 * The priority if the event executor is {@link EventPriority#NORMAL}.
	 * Does not ignore cancelled events.
	 * @param eventClass the class of the event to listen to.
	 * @param eventExecutor the executor.
	 * @return the {@link Listener} instance, that is in our case the provided executor.
	 * @param <E> the event type.
	 */
	public static <E extends Event> Listener register(Class<E> eventClass, EventListener<E> eventExecutor) {
		return register(eventClass, eventExecutor, EventPriority.NORMAL, false);
	}

	/**
	 * Register a single event executor.
	 * <p>
	 * Does not ignore cancelled events.
	 * @param eventClass the class of the event to listen to.
	 * @param eventExecutor the executor.
	 * @param priority the event priority.
	 * @return the {@link Listener} instance, that is in our case the provided executor.
	 * @param <E> the event type.
	 */
	public static <E extends Event> Listener register(Class<E> eventClass, EventListener<E> eventExecutor, EventPriority priority) {
		return register(eventClass, eventExecutor, priority, false);
	}

	/**
	 * Register a single event executor.
	 * <p>
	 * The priority if the event executor is {@link EventPriority#NORMAL}.
	 * @param eventClass the class of the event to listen to.
	 * @param eventExecutor the executor.
	 * @param ignoreCancelled whether to pass cancelled events or not.
	 * @return the {@link Listener} instance, that is in our case the provided executor.
	 * @param <E> the event type.
	 */
	public static <E extends Event> Listener register(Class<E> eventClass, EventListener<E> eventExecutor, boolean ignoreCancelled) {
		return register(eventClass, eventExecutor, EventPriority.NORMAL, ignoreCancelled);
	}

	/**
	 * Register a single event executor.
	 * @param eventClass the class of the event to listen to.
	 * @param eventExecutor the executor.
	 * @param priority the event priority.
	 * @param ignoreCancelled whether to pass cancelled events or not.
	 * @return the {@link Listener} instance, that is in our case the provided executor.
	 * @param <E> the event type.
	 */
	public static <E extends Event> Listener register(Class<E> eventClass, EventListener<E> eventExecutor, EventPriority priority, boolean ignoreCancelled) {
		Bukkit.getPluginManager().registerEvent(eventClass, eventExecutor, priority, eventExecutor, PandaLibPaper.getPlugin(), ignoreCancelled);
		return eventExecutor;
	}

	/**
	 * Register a listener.
	 * This is equivalent to calling {@code Bukkit.getPluginManager().registerEvents(l, PandaLibPaper.getPlugin());}
	 * @param l the listener.
	 */
	public static void register(Listener l) {
		Bukkit.getPluginManager().registerEvents(l, PandaLibPaper.getPlugin());
	}


	/**
	 * Unregister a listener
	 * @param listener the listener to unregister.
	 */
	public static void unregister(Listener listener) {
		HandlerList.unregisterAll(listener);
	}


	/**
	 * Lists all existing subclasses of {@link Event}.
	 * @return a list of all existing subclasses of {@link Event}.
	 */
	public static List<Class<? extends Event>> getAllEventClasses() {
		List<Class<? extends Event>> classes = Reflect.ofClass(Event.class).getAllSubclasses(false);
		classes.removeIf(e -> getHandlerList(e) == null);
		return classes;
	}


	/**
	 * Gets the handlerList of the provided Event class.
	 * @param type the event class.
	 * @return the handlerList.
	 */
    // method retrieved from OB.plugin.SimplePluginManager#getEventListeners
    public static HandlerList getHandlerList(Class<? extends Event> type) {
        try {
			Class<? extends Event> actualClass = getRegistrationClass(type);
			if (actualClass == null)
				return null;
            return (HandlerList) Reflect.ofClass(actualClass).method("getHandlerList").invokeStatic();
        }
        catch (ReflectiveOperationException e) {
            return null;
        }
    }

    // method retrieved from OB.plugin.SimplePluginManager
    private static Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) {
        try {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        }
        catch (NoSuchMethodException e) {	
            if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Event.class) && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            }
            return null;
        }
    }


	/**
	 * An single executor event listener. Used for the {@link #register(Class, EventListener)} static method and the other variants.
	 * @param <E> the event type.
	 */
	public interface EventListener<E extends Event> extends Listener, EventExecutor {

		/**
		 * The event handler.
		 * @param event the event.
		 */
		void onEvent(E event);
		
		@SuppressWarnings("unchecked")
		@Override
		default void execute(Listener var1, Event var2) throws EventException {
			onEvent((E)var2);
		}
	}
	
	/**
	 * Abstract implementation of {@link EventListener} that ensure as good as it can,
	 * that it is the last listener called to handle the event.
	 *
	 * @param <E> the type of the event.
	 */
	public static abstract class EnforcedLastListener<E extends Event> implements EventListener<E> {
		private final Class<E> eventClass;
		private final boolean ignoreCancelled;

		/**
		 * Creates a new {@link EnforcedLastListener}.
		 * @param eventClass the event to listen.
		 * @param ignoreCancelled whether to pass cancelled events or not.
		 */
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
		
		
		private final AtomicReference<BukkitTask> listenerCheckTask = new AtomicReference<>();
		
		private void checkIfListenerIsLast() {
			synchronized (listenerCheckTask) {
				if (listenerCheckTask.get() != null)
					return;
				HandlerList hList = BukkitEvent.getHandlerList(eventClass);
				if (hList == null)
					return;
				RegisteredListener[] listeners = hList.getRegisteredListeners();
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


	private BukkitEvent() {}
	

}
