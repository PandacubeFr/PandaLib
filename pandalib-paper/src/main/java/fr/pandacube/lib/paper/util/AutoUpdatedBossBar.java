package fr.pandacube.lib.paper.util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.util.log.Log;
import fr.pandacube.lib.paper.PandaLibPaper;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.text.Component;

public class AutoUpdatedBossBar implements Listener {
	
	public final BossBar bar;
	public final BarUpdater updater;
	
	private Timer timer = null;
	private BukkitTask bukkitTask = null;
	
	private boolean scheduled = false;
	
	private boolean followPlayerList = false;
	private Predicate<Player> playerCondition = null;
	
	public AutoUpdatedBossBar(BossBar bar, BarUpdater updater) {
		this.bar = bar;
		this.updater = updater;
	}
	
	
	/**
	 * Schedule the update of this boss bar with synchronisation with the system clock.
	 * The underlying method called is {@link Timer#schedule(TimerTask, long, long)}.
	 * The updater is executed in a separate Thread.
	 * @param msDelay ms before running the first update of this bossbar
	 * @param msPeriod ms between each call of the updater
	 */
	public synchronized void scheduleUpdateTimeSyncThreadAsync(long msDelay, long msPeriod) {
		if (scheduled)
			throw new IllegalStateException("Can't schedule an already scheduled boss bar update");
		
		scheduled = true;
		timer = new Timer("Panda BossBarUpdater - " + Chat.chatComponent(bar.name()).getPlainText());
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					updater.update(AutoUpdatedBossBar.this);
				} catch(Throwable e) {
					Log.severe("Error while updating an AutoUpdatedBossBar", e);
				}
			}
		}, msDelay, msPeriod);
	}
	
	/**
	 * Schedule the update of this boss bar with synchronisation with the main Thread of the
	 * current Minecraft server (follow the tick count progress).
	 * The underlying method called is {@link BukkitScheduler#runTaskTimer(org.bukkit.plugin.Plugin, Runnable, long, long)}.
	 * The updater is executed by the Server Thread.
	 * @param tickDelay number of server tick before running the first update of this boss bar
	 * @param tickPeriod number of server tick between each call of the updater
	 */
	public synchronized void scheduleUpdateTickSyncThreadSync(long tickDelay, long tickPeriod) {
		if (scheduled)
			throw new IllegalStateException("Can't schedule an already scheduled boss bar update");
		
		scheduled = true;
		bukkitTask = Bukkit.getServer().getScheduler()
				.runTaskTimer(PandaLibPaper.getPlugin(), () -> {
					synchronized (bar) {
						try {
							updater.update(this);
						} catch(Throwable e) {
							Log.severe("Error while updating an AutoUpdatedBossBar", e);
						}
					}
				}, tickDelay, tickPeriod);
		
	}
	
	
	
	public synchronized void followLoginLogout(Predicate<Player> condition) {
		playerCondition = condition;
		if (followPlayerList)
			return;
		followPlayerList = true;
		BukkitEvent.register(this);
		Bukkit.getServer().getOnlinePlayers().forEach(p -> onPlayerJoin(new PlayerJoinEvent(p, Component.text(""))));
	}
	
	public synchronized void unfollowPlayerList() {
		if (!followPlayerList)
			return;
		followPlayerList = false;
		playerCondition = null;
		PlayerJoinEvent.getHandlerList().unregister(this);
		PlayerQuitEvent.getHandlerList().unregister(this);
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public synchronized void onPlayerJoin(PlayerJoinEvent event) {
		if (!followPlayerList)
			return;
		if (playerCondition != null && !playerCondition.test(event.getPlayer()))
			return;
		synchronized (bar) {
			event.getPlayer().showBossBar(bar);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public synchronized void onPlayerQuit(PlayerQuitEvent event) {
		if (!followPlayerList)
			return;
		synchronized (bar) {
			event.getPlayer().hideBossBar(bar);
		}
	}

	public void removeAll() {
		synchronized (bar) {
			for (Player p : Bukkit.getOnlinePlayers())
				p.hideBossBar(bar);
		}
	}
	
	
	public synchronized void cancel() {
		if (!scheduled)
			return;
		scheduled = false;
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (bukkitTask != null) {
			bukkitTask.cancel();
			bukkitTask = null;
		}
		
	}
	
	
	
	@FunctionalInterface
	public interface BarUpdater {
		void update(AutoUpdatedBossBar bar);
	}
	

	
	/**
	 * Utility method to update the title of the boss bar without unnecessary packet.
	 */
	public void setTitle(Chat title) {
		synchronized (bar) {
			bar.name(title); // already check if the title is the same
		}
	}
	
	/**
	 * Utility method to update the color of the boss bar without unnecessary packet.
	 */
	public void setColor(Color color) {
		synchronized (bar) {
			bar.color(color);
		}
	}
	
	/**
	 * Utility method to update the progress of the boss bar without unnecessary packet.
	 */
	public void setProgress(double progress) {
		synchronized (bar) {
			bar.progress((float) progress);
		}
	}
	
	
	
	
	

}
