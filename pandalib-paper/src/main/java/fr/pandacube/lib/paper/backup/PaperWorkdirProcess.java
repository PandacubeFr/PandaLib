package fr.pandacube.lib.paper.backup;

import java.io.File;
import java.util.function.BiPredicate;

/**
 * A backup process with specific logic around Paper server working directory.
 */
public class PaperWorkdirProcess extends PaperBackupProcess {

	/**
	 * Instantiates a new backup process for the paper server working directory.
	 * @param bm the associated backup manager.
	 */
	protected PaperWorkdirProcess(PaperBackupManager bm) {
		super(bm, "workdir");
	}


	public BiPredicate<File, String> getFilenameFilter() {
		return (file, path) -> {
			if (file.isDirectory() && new File(file, "level.dat").exists())
				return false;
			if (new File(getSourceDir(), "logs").equals(file))
				return false;
			if (file.isFile() && file.getName().endsWith(".lck"))
				return false;
			return PaperWorkdirProcess.super.getFilenameFilter().test(file, path);
		};
	}
	

	
	@Override
	public File getSourceDir() {
		return new File(".");
	}
	
	@Override
	protected void onBackupEnd(boolean success) {
		if (success)
			setDirtySinceNow();
		super.onBackupEnd(success);
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
