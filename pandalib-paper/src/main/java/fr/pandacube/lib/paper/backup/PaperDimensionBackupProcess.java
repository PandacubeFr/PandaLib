package fr.pandacube.lib.paper.backup;

import fr.pandacube.lib.chat.LegacyChatFormat;
import fr.pandacube.lib.paper.scheduler.SchedulerUtil;
import fr.pandacube.lib.paper.world.ServerDimensionDir;
import fr.pandacube.lib.util.log.Log;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

/**
 * A backup process with specific logic around a dimension.
 */
public class PaperDimensionBackupProcess extends PaperBackupProcess {
	private final NamespacedKey dimensionKey;
	
	private boolean autoSave = true;

	/**
	 * Instantiates a new backup process for a dimension.
	 * @param bm the associated backup manager.
	 * @param dimensionKey the key of the dimension.
	 */
	protected PaperDimensionBackupProcess(PaperBackupManager bm, final NamespacedKey dimensionKey) {
		super(bm, "dimension/" + dimensionKey.toString());
		this.dimensionKey = dimensionKey;
	}
	
	private World getWorld() {
		return Bukkit.getWorld(dimensionKey);
	}
	
	
	@Override
	public File getSourceDir() {
		return ServerDimensionDir.fromServerLevel(dimensionKey).getDirectory();
	}
	
	@Override
	protected void onBackupStart() {
		World w = getWorld();
		if (w != null) {
			autoSave = w.isAutoSave();
			w.setAutoSave(false);
		}
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
		return new File(getBackupManager().getBackupDirectory(), "dimensions/" + dimensionKey.getNamespace() + "/" + dimensionKey.getKey());
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
	 * Make the specified dimension dirty for backup, and makes it clean for saving if nobody is connected there.
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
