package fr.pandacube.lib.paper.backup;

import fr.pandacube.lib.core.backup.BackupManager;
import fr.pandacube.lib.core.backup.BackupProcess;
import fr.pandacube.lib.core.backup.RotatedLogsBackupProcess;
import fr.pandacube.lib.paper.PandaLibPaper;
import fr.pandacube.lib.paper.scheduler.SchedulerUtil;
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

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;

/**
 * The backup manager for Paper servers.
 */
public class PaperBackupManager extends BackupManager implements Listener {

	private final Map<String, PaperWorldProcess> compressWorlds = new HashMap<>();

	PaperBackupConfig config;

	/**
	 * Instantiate a new backup manager.
	 * @param config the configuration of the backups.
	 */
	public PaperBackupManager(PaperBackupConfig config) {
		super(config.backupDirectory);
		setConfig(config);


		for (final World world : Bukkit.getWorlds()) {
			initWorldProcess(world.getName());
		}
		addProcess(new PaperWorkdirProcess(this));
		addProcess(new RotatedLogsBackupProcess(this, true, new File("logs"), "[0-9]{4}-[0-9]{2}-[0-9]{2}(-[0-9]+)?\\.log\\.gz"));

		Bukkit.getServer().getPluginManager().registerEvents(this, PandaLibPaper.getPlugin());
	}

	@Override
	protected void addProcess(BackupProcess process) {
		updateProcessConfig(process);
		super.addProcess(process);
	}

	/**
	 * Updates the backups config
	 * @param config the new config.
	 */
	public void setConfig(PaperBackupConfig config) {
		this.config = config;
		backupQueue.forEach(this::updateProcessConfig);
	}


	private void updateProcessConfig(BackupProcess process) {
		if (process instanceof PaperWorkdirProcess) {
			process.setEnabled(config.workdirBackupEnabled);
			process.setBackupCleaner(config.workdirBackupCleaner);
			process.setScheduling(config.scheduling);
			process.setIgnoreList(config.workdirIgnoreList);
		}
		else if (process instanceof PaperWorldProcess) {
			process.setEnabled(config.worldBackupEnabled);
			process.setBackupCleaner(config.worldBackupCleaner);
			process.setScheduling(config.scheduling);
		}
		else if (process instanceof RotatedLogsBackupProcess) {
			process.setEnabled(config.logsBackupEnabled);
		}
	}


	@Override
	public void run() {
		try {
			SchedulerUtil.runOnServerThreadAndWait(super::run);
		} catch (CancellationException ignored) {

        } catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void onDisable() {

		// save dirty status of worlds
		for (String wName : dirtyForSave) {
			World w = Bukkit.getWorld(wName);
			if (w != null)
				compressWorlds.get(w.getName()).setDirtyAfterSave();
		}

		super.onDisable();
	}
	
	private void initWorldProcess(final String worldName) {
		if (compressWorlds.containsKey(worldName))
			return;
		PaperWorldProcess process = new PaperWorldProcess(this, worldName);
		addProcess(process);
		compressWorlds.put(worldName, process);
	}













	private final Set<String> dirtyForSave = new HashSet<>();

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldLoad(WorldLoadEvent event) {
		initWorldProcess(event.getWorld().getName());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldSave(WorldSaveEvent event) {
		if (event.getWorld().getLoadedChunks().length > 0
				|| dirtyForSave.contains(event.getWorld().getName())) {
			compressWorlds.get(event.getWorld().getName()).setDirtyAfterSave();
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
