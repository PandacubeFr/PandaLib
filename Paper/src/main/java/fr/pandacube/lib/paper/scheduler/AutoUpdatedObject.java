package fr.pandacube.lib.paper.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import fr.pandacube.lib.paper.PandaLibPaper;

public class AutoUpdatedObject {
	private static Plugin plugin = PandaLibPaper.getPlugin();
	
	private Runnable updater;
	
	private List<BukkitTask> tasks = new ArrayList<>();
	

	protected AutoUpdatedObject() { }
	public AutoUpdatedObject(Runnable updater) {
		this.updater = Objects.requireNonNull(updater);
	}
	
	public synchronized void updateSync() {
		tasks.add(Bukkit.getScheduler().runTask(plugin, this::update));
	}
	
	public synchronized void updateAsync() {
		tasks.add(Bukkit.getScheduler().runTaskAsynchronously(plugin, this::update));
	}
	
	public synchronized void updateLater(long delay)
			throws IllegalArgumentException, IllegalStateException {
		tasks.add(Bukkit.getScheduler().runTaskLater(plugin, this::update, delay));
	}
	
	public synchronized void updateLaterAsync(long delay)
			throws IllegalArgumentException, IllegalStateException {
		tasks.add(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, this::update, delay));
	}
	
	public synchronized void updateTimer(long delay, long period)
			throws IllegalArgumentException, IllegalStateException {
		tasks.add(Bukkit.getScheduler().runTaskTimer(plugin, this::update, delay, period));
	}
	
	public synchronized void updateTimerAsync(long delay, long period)
			throws IllegalArgumentException, IllegalStateException {
		tasks.add(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::update, delay, period));
	}
	
	public synchronized void cancel() {
		tasks.forEach(t -> t.cancel());
		tasks.clear();
	}

	public void update() {
		Objects.requireNonNull(updater, "Please use new AutoUpdatedObject(Runnable) or override the run method.");
		updater.run();
	}

}
