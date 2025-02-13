package fr.pandacube.lib.bungee.backup;

import fr.pandacube.lib.core.backup.BackupProcess;

import java.io.File;
import java.util.function.BiPredicate;

/**
 * The backup process responsible for the working directory of the current BungeeCord instance.
 */
public class BungeeWorkdirProcess extends BackupProcess {

	/**
	 * Instantiates this backup process.
	 * @param bm the backup manager.
	 */
	protected BungeeWorkdirProcess(BungeeBackupManager bm) {
		super(bm, "workdir");
	}

	@Override
	public BungeeBackupManager getBackupManager() {
		return (BungeeBackupManager) super.getBackupManager();
	}
	
	
	public BiPredicate<File, String> getFilenameFilter() {
		return (file, path) -> {
			if (new File(getSourceDir(), "logs").equals(file))
				return false;
			if (file.isFile() && file.getName().endsWith(".lck"))
				return false;
			return BungeeWorkdirProcess.super.getFilenameFilter().test(file, path);
		};
	}
	

	
	@Override
	public File getSourceDir() {
		return new File(".");
	}

	@Override
	protected void onBackupStart() { }
	
	@Override
	protected void onBackupEnd(boolean success) {
		if (success)
			setDirtySinceNow();
	}

	@Override
	protected File getTargetDir() {
		return new File(getBackupManager().getBackupDirectory(), "workdir");
	}

	@Override
	protected String getDisplayName() {
		return "workdir";
	}

}
