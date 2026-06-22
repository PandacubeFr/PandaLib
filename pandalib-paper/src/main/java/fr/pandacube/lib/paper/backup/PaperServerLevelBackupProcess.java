package fr.pandacube.lib.paper.backup;

import fr.pandacube.lib.paper.world.LevelDir;

import java.io.File;
import java.util.function.BiPredicate;

/**
 * A backup process for a vanilla/Paper Level directory, but excluding the dimensions directory.
 */
public class PaperServerLevelBackupProcess extends PaperBackupProcess {

	/**
	 * Instantiates a new backup process for the level directory.
	 * @param bm the associated backup manager.
	 */
	protected PaperServerLevelBackupProcess(PaperBackupManager bm) {
		super(bm, "serverlevel");
	}


	public BiPredicate<File, String> getFilenameFilter() {
		return (file, path) -> {
			if (new File(getSourceDir(), "dimensions").equals(file))
				return false;
			return PaperServerLevelBackupProcess.super.getFilenameFilter().test(file, path);
		};
	}
	

	
	@Override
	public File getSourceDir() {
		return LevelDir.ofServer().getDirectory();
	}
	
	@Override
	protected void onBackupEnd(boolean success) {
		if (success)
			setDirtySinceNow();
		super.onBackupEnd(success);
	}

	@Override
	protected File getTargetDir() {
		return new File(getBackupManager().getBackupDirectory(), "serverlevel");
	}

	@Override
	protected String getDisplayName() {
		return "serverlevel";
	}

}
