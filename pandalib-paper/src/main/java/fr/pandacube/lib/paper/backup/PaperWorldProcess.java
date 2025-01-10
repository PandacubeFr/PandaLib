package fr.pandacube.lib.paper.backup;

import fr.pandacube.lib.chat.LegacyChatFormat;
import fr.pandacube.lib.paper.scheduler.SchedulerUtil;
import fr.pandacube.lib.paper.world.WorldUtil;
import fr.pandacube.lib.util.log.Log;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

/**
 * A backup process with specific logic around Paper server world.
 */
public class PaperWorldProcess extends PaperBackupProcess {
	private final String worldName;
	
	private boolean autoSave = true;

	/**
	 * Instantiates a new backup process for a world.
	 * @param bm the associated backup manager.
	 * @param worldName the name of the world.
	 */
	protected PaperWorldProcess(PaperBackupManager bm, final String worldName) {
		super(bm, "worlds/" + worldName);
		this.worldName = worldName;
	}
	
	private World getWorld() {
		return Bukkit.getWorld(worldName);
	}
	
	
	@Override
	public File getSourceDir() {
		return WorldUtil.worldDir(worldName);
	}
	
	@Override
	protected void onBackupStart() {
		World w = getWorld();
		if (w == null)
			return;
		autoSave = w.isAutoSave();
		w.setAutoSave(false);
		super.onBackupStart();
	}
	
	@Override
	protected void onBackupEnd(boolean success) {
		if (success)
			setNotDirty();
		SchedulerUtil.runOnServerThread(() -> {
			World w = getWorld();
			if (w == null)
				return;
			w.setAutoSave(autoSave);
		});
		super.onBackupEnd(success);
	}

	@Override
	protected File getTargetDir() {
		return new File(getBackupManager().getBackupDirectory(), "worlds/" + worldName);
	}


	public void displayNextSchedule() {
		if (hasNextScheduled()) {
			Log.info("[Backup] " + LegacyChatFormat.GRAY + getDisplayName() + LegacyChatFormat.RESET + " is dirty. Next backup on "
					+ DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date(getNext())));
		}
		else {
			Log.info("[Backup] " + LegacyChatFormat.GRAY + getDisplayName() + LegacyChatFormat.RESET + " is clean. Next backup not scheduled.");
		}
	}





	/**
	 * Make the specified world dirty for compress. Also makes the specified world clean for saving if nobody is connected there.
	 */
	public void setDirtyAfterSave() {
		if (!isDirty()) { // don't set dirty if it is already
			setDirtySinceNow();
			Log.info("[Backup] " + LegacyChatFormat.GRAY + getDisplayName() + LegacyChatFormat.RESET + " was saved and is now dirty. Next backup on "
					+ DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG)
					.format(new Date(getNext()))
			);
		}
	}
}
