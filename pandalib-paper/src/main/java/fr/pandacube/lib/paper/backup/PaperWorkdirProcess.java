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
				if (globalExcluded(file, path))
					return false;
				for (String exclude : getBackupManager().config.workdirIgnoreList) {
					if (exclude.startsWith("/")) { // relative to source of workdir
						if (path.matches(exclude.substring(1)))
							return false;
					}
					else {
						String name = path.substring(path.lastIndexOf("/") + 1);
						if (name.matches(exclude))
							return false;
					}
				}
				return true;
			}

			public boolean globalExcluded(File file, String path) {
				if (file.isDirectory() && new File(file, "level.dat").exists())
					return true;
				if (new File(getSourceDir(), "logs").equals(file))
					return true;
				if (file.isFile() && file.getName().endsWith(".lck"))
					return true;
				return false;
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



	public void displayNextSchedule() {
		Log.info("[Backup] " + net.md_5.bungee.api.ChatColor.GRAY + getDisplayName() + net.md_5.bungee.api.ChatColor.RESET + " next backup on "
				+ DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date(getNext())));
	}
}
