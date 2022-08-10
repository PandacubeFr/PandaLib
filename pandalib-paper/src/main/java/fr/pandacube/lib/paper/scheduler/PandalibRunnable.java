package fr.pandacube.lib.paper.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import fr.pandacube.lib.paper.PandaLibPaper;

/**
 * An extension of {@link BukkitRunnable} that already integrates a reference to the Bukkit plugin.
 */
public class PandalibRunnable extends BukkitRunnable {
	private static final Plugin plugin = PandaLibPaper.getPlugin();
	
	private Runnable updater;


	/**
	 * Instanciate a {@link PandalibRunnable}, whose {@link #run()} method will be called by the server scheduler.
	 * When using this default constructor, the {@link #run()} method must be override to provides code to run.
	 */
	protected PandalibRunnable() { }

	/**
	 * Instanciate a {@link PandalibRunnable}, with an {@code updater} that will be called by the server scheduler.
	 * @param updater the updater to run when this task is executed.
	 */
	public PandalibRunnable(Runnable updater) {
		this.updater = Objects.requireNonNull(updater);
	}


	/**
	 * Same as {@link #runTask(Plugin)}, but the plugin instance is already provided by
	 * {@link PandaLibPaper#getPlugin()}.
	 * @return a {@link BukkitTask} that contains the id number.
	 */
	public synchronized BukkitTask runTask() {
		return runTask(plugin);
	}

	/**
	 * Same as {@link #runTaskAsynchronously(Plugin)}, but the plugin instance is already provided by
	 * {@link PandaLibPaper#getPlugin()}.
	 * @return a {@link BukkitTask} that contains the id number.
	 */
	public synchronized BukkitTask runTaskAsynchronously() {
		return runTaskAsynchronously(plugin);
	}

	/**
	 * Same as {@link #runTaskLater(Plugin, long)}, but the plugin instance is already provided by
	 * {@link PandaLibPaper#getPlugin()}.
	 * @param delay the ticks to wait before running the task.
	 * @return a {@link BukkitTask} that contains the id number.
	 */
	public synchronized BukkitTask runTaskLater(long delay) {
		return runTaskLater(plugin, delay);
	}

	/**
	 * Same as {@link #runTaskLaterAsynchronously(Plugin, long)}, but the plugin instance is already provided by
	 * {@link PandaLibPaper#getPlugin()}.
	 * @param delay the ticks to wait before running the task.
	 * @return a {@link BukkitTask} that contains the id number.
	 */
	public synchronized BukkitTask runTaskLaterAsynchronously(long delay) {
		return runTaskLaterAsynchronously(plugin, delay);
	}

	/**
	 * Same as {@link #runTaskTimer(Plugin, long, long)}, but the plugin instance is already provided by
	 * {@link PandaLibPaper#getPlugin()}.
	 * @param delay the ticks to wait before running the task.
	 * @param period the ticks to wait between runs.
	 * @return a {@link BukkitTask} that contains the id number.
	 */
	public synchronized BukkitTask runTaskTimer(long delay, long period) {
		return runTaskTimer(plugin, delay, period);
	}

	/**
	 * Same as {@link #runTaskTimerAsynchronously(Plugin, long, long)}, but the plugin instance is already provided by
	 * {@link PandaLibPaper#getPlugin()}.
	 * @param delay the ticks to wait before running the task.
	 * @param period the ticks to wait between runs.
	 * @return a {@link BukkitTask} that contains the id number.
	 */
	public synchronized BukkitTask runTaskTimerAsynchronously(long delay, long period) {
		return runTaskTimerAsynchronously(plugin, delay, period);
	}

	@Override
	public void run() {
		Objects.requireNonNull(updater, "Please use new PandalibRunnable(Runnable) or override the run method.")
				.run();
	}

}
