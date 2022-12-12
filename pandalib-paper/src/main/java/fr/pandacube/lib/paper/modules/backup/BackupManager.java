package fr.pandacube.lib.paper.modules.backup;

import fc.cron.CronExpression;
import fr.pandacube.lib.paper.PandaLibPaper;
import fr.pandacube.lib.util.Log;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class BackupManager implements Runnable, Listener {
	

	
	Persist persist;
	
	private final List<CompressProcess> compressQueue = new ArrayList<>();
	
	private final Set<String> compressWorlds = new HashSet<>();
	
	/* package */ AtomicReference<CompressProcess> compressRunning = new AtomicReference<>();

	private final Set<String> dirtyForSave = new HashSet<>();

	BackupConfig config;
	
	public BackupManager(BackupConfig config) {
		setConfig(config);
		persist = new Persist(this);

		
		for (final World world : Bukkit.getWorlds()) {
			initCompressProcess(Type.WORLDS, world.getName());
		}
		
		initCompressProcess(Type.WORKDIR, null);
		
		Bukkit.getServer().getScheduler().runTaskTimer(PandaLibPaper.getPlugin(), this, (60 - Calendar.getInstance().get(Calendar.SECOND)) * 20L, 60 * 20L);
		
		Bukkit.getServer().getPluginManager().registerEvents(this, PandaLibPaper.getPlugin());
		
	}

	public void setConfig(BackupConfig config) {
		this.config = config;
	}
	
	
	public void onDisable() {
		
		if (compressRunning.get() != null) {
			Log.warning("[Backup] Waiting after the end of a backup...");
			CompressProcess tmp;
			while ((tmp = compressRunning.get()) != null) {
				try {
					tmp.logProgress();
					// wait 5 seconds between each progress log
					// but check if the process has ended each .5 seconds
					for (int i = 0; i < 10; i++) {
						if (compressRunning.get() == null)
							break;
						Thread.sleep(500);
					}
				} catch (Throwable e) { // could occur because of synchronization errors/interruption/...
					break;
				}
			}
		}

		// save dirty status of worlds
		for (String wName : dirtyForSave) {
			World w = Bukkit.getWorld(wName);
			if (w != null)
				persist.updateDirtyStatusAfterSave(w);
		}
		
		persist.save();
	}
	
	private void initCompressProcess(final Type type, final String worldName) {
		if (!type.backupEnabled(config))
			return;
		if (type == Type.WORLDS) {
			if (compressWorlds.contains(worldName))
				return;
			compressWorlds.add(worldName);
		}
		CompressProcess process = type == Type.WORLDS ? new CompressWorldProcess(this, worldName) : new CompressWorkdirProcess(this);
		process.displayDirtynessStatus();
		compressQueue.add(process);
	}
	
	@Override
	public void run() {
		CompressProcess tmp;
		if ((tmp = compressRunning.get()) != null) {
			tmp.logProgress();
		}
		else {
			compressQueue.sort(null);
			for (CompressProcess process : compressQueue) {
				if (System.currentTimeMillis() >= process.getNext() && process.couldRunNow()) {
					process.run();
					return;
				}
			}
		}
	}





	/**
	 * get the timestamp (in ms) of when the next compress will run, depending on since when the files to compress are dirty.
	 * @param dirtySince the timestamp in ms since the files are dirty
	 * @return the timestamp in ms when the next compress of the files should be run, or 0 if it is not yet scheduled
	 */
	/* package */ long getNextCompress(long dirtySince) {
		if (dirtySince == -1)
			return 0;

		CronExpression parsedScheduling;
		try {
			parsedScheduling = new CronExpression(config.scheduling, false);
		} catch (IllegalArgumentException e) {
			Log.severe("Invalid backup scheduling configuration '" + config.scheduling + "'.", e);
			return 0;
		}

		ZonedDateTime ldt = parsedScheduling.nextTimeAfter(ZonedDateTime.from(Instant.ofEpochMilli(dirtySince)));
		// Log.info("Compress config: " + compressConfig + " - interval: " + interval);

		return ldt.toInstant().toEpochMilli();
	}
	
	
	
	
	
	
	
	

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldLoad(WorldLoadEvent event) {
		initCompressProcess(Type.WORLDS, event.getWorld().getName());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldSave(WorldSaveEvent event) {
		if (event.getWorld().getLoadedChunks().length > 0
				|| dirtyForSave.contains(event.getWorld().getName())) {
			persist.updateDirtyStatusAfterSave(event.getWorld());
			dirtyForSave.remove(event.getWorld().getName());
		}
	}





	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChangeWorldEvent(PlayerChangedWorldEvent event) {
		dirtyForSave.add(event.getFrom().getName());
		dirtyForSave.add(event.getPlayer().getWorld().getName());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		dirtyForSave.add(event.getPlayer().getWorld().getName());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		dirtyForSave.add(event.getPlayer().getWorld().getName());
	}


	
}
