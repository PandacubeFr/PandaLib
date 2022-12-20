package fr.pandacube.lib.paper.backup;

import fr.pandacube.lib.paper.scheduler.SchedulerUtil;
import fr.pandacube.lib.paper.util.WorldUtil;
import fr.pandacube.lib.util.Log;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.function.BiPredicate;

public class PaperWorldProcess extends PaperBackupProcess {
	private final String worldName;
	
	private boolean autoSave = true; 
	
	protected PaperWorldProcess(PaperBackupManager bm, final String n) {
		super(bm, "worlds/" + n);
		worldName = n;
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
			Log.info("[Backup] " + ChatColor.GRAY + getDisplayName() + ChatColor.RESET + " is dirty. Next backup on "
					+ DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date(getNext())));
		}
		else {
			Log.info("[Backup] " + ChatColor.GRAY + getDisplayName() + ChatColor.RESET + " is clean. Next backup not scheduled.");
		}
	}





	/**
	 * Make the specified world dirty for compress. Also makes the specified world clean for saving if nobody is connected there.
	 */
	public void setDirtyAfterSave() {
		if (!isDirty()) { // don't set dirty if it is already
			setDirtySinceNow();
			Log.info("[Backup] " + ChatColor.GRAY + getDisplayName() + ChatColor.RESET + " was saved and is now dirty. Next backup on "
					+ DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG)
					.format(new Date(getNext()))
			);
		}
	}
}
