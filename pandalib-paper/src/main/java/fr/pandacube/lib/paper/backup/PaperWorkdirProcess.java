package fr.pandacube.lib.paper.backup;

import fr.pandacube.lib.util.Log;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.function.BiPredicate;

public class PaperWorkdirProcess extends PaperBackupProcess {
	
	protected PaperWorkdirProcess(PaperBackupManager bm) {
		super(bm, "workdir");
	}


	public BiPredicate<File, String> getFilenameFilter() {
		return new BiPredicate<File, String>() {
			@Override
			public boolean test(File file, String path) {
				if (file.isDirectory() && new File(file, "level.dat").exists())
					return false;
				if (new File(getSourceDir(), "logs").equals(file))
					return false;
				if (file.isFile() && file.getName().endsWith(".lck"))
					return false;
				return PaperWorkdirProcess.super.getFilenameFilter().test(file, path);
			}
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
