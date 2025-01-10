package fr.pandacube.lib.paper.util;

import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.paper.PandaLibPaper;
import fr.pandacube.lib.util.log.Log;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Predicate;

/**
 * A {@link BossBar} capable of automatically updating itself by registering a {@link BukkitTask} or using a {@link Timer}.
 */
public class AutoUpdatedBossBar implements Listener {

	/**
	 * The boss bar itself.
	 */
	public final BossBar bar;
	/**
	 * the function executed to update the boss bar.
	 */
	public final BarUpdater updater;

	private Timer timer = null;
	private BukkitTask bukkitTask = null;
	
	private boolean scheduled = false;
	
	private LoginLogoutListener followPlayerList = null;
	private Predicate<Player> playerCondition = null;

	/**
	 * Wraps the provided boss bar into a new instance of {@link AutoUpdatedBossBar}.
	 * @param bar the boss bar itself.
	 * @param updater the updater that will be run to update the boss bar.
	 */
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
	 * Schedule the update of this boss bar with synchronisation with the ticking of this Minecraft server.
	 * The underlying method called is {@link BukkitScheduler#runTaskTimer(org.bukkit.plugin.Plugin, Runnable, long, long)}.
	 * The updater is executed by the main Server Thread.
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


	/**
	 * Auto-update the boss bar on player join and quit.
	 * @param condition an additional test that if it's true on player join/quit, will actually run the update. Can be null.
	 */
	public synchronized void followLoginLogout(Predicate<Player> condition) {
		playerCondition = condition;
		if (followPlayerList != null)
			return;
		followPlayerList = new LoginLogoutListener();
		BukkitEvent.register(followPlayerList);
		Bukkit.getServer().getOnlinePlayers().forEach(p -> followPlayerList.onPlayerJoin(new PlayerJoinEvent(p, Component.text(""))));
	}

	/**
	 * Cancel the auto-update on player join and quit.
	 */
	public synchronized void unfollowPlayerList() {
		if (followPlayerList == null)
			return;
		playerCondition = null;
		BukkitEvent.unregister(followPlayerList);
		followPlayerList = null;
	}

	private class LoginLogoutListener implements Listener {

		@EventHandler(priority=EventPriority.MONITOR)
		public void onPlayerJoin(PlayerJoinEvent event) {
			if (playerCondition != null && !playerCondition.test(event.getPlayer()))
				return;
			synchronized (bar) {
				event.getPlayer().showBossBar(bar);
			}
		}

		@EventHandler(priority=EventPriority.HIGH)
		public void onPlayerQuit(PlayerQuitEvent event) {
			synchronized (bar) {
				event.getPlayer().hideBossBar(bar);
			}
		}
	}


	/**
	 * Hides this boss bar from all players.
	 */
	public void removeAll() {
		synchronized (bar) {
			for (Player p : Bukkit.getOnlinePlayers())
				p.hideBossBar(bar);
		}
	}

	/**
	 * Cancel any auto-updating of this boss bar.
	 */
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
		unfollowPlayerList();
	}


	/**
	 * Functional interface taking an instance of {@link AutoUpdatedBossBar} to update it. Returns nothing.
	 */
	@FunctionalInterface
	public interface BarUpdater {
		/**
		 * Updates the boss bar.
		 * @param bar the auto-updated boss bar instance.
		 */
		void update(AutoUpdatedBossBar bar);
	}
	

	
	/**
	 * Utility method to update the title of the boss bar without unnecessary packet.
	 * @param title the new title.
	 */
	public void setTitle(Chat title) {
		synchronized (bar) {
			bar.name(title); // already check if the title is the same
		}
	}
	
	/**
	 * Utility method to update the color of the boss bar without unnecessary packet.
	 * @param color the new color.
	 */
	public void setColor(Color color) {
		synchronized (bar) {
			bar.color(color);
		}
	}
	
	/**
	 * Utility method to update the progress of the boss bar without unnecessary packet.
	 * @param progress the new progress value.
	 */
	public void setProgress(double progress) {
		synchronized (bar) {
			bar.progress((float) progress);
		}
	}
	
	
	
	
	

}
