package fr.pandacube.lib.paper.modules.backup;

import java.io.File;
import java.util.function.BiPredicate;

public class CompressWorkdirProcess extends CompressProcess {
	
	protected CompressWorkdirProcess(BackupManager bm) {
		super(bm, Type.WORKDIR, Type.WORKDIR.toString());
	}
	
	
	public BiPredicate<File, String> getFilenameFilter() {
		return new SourceFileFilter();
	}
	
	
	
	private class SourceFileFilter implements BiPredicate<File, String> {


		@Override
		public boolean test(File file, String path) {
			if (globalExcluded(file, path))
				return false;
			for (String exclude : backupManager.config.workdirIgnoreList) {
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

	}
	

	
	@Override
	public File getSourceDir() {
		return new File(".");
	}
	
	@Override
	protected void onCompressStart() { }
	
	@Override
	protected void onCompressEnd(boolean success) { }

	@Override
	protected File getTargetDir() {
		return new File(backupManager.config.backupDirectory, "workdir");
	}
}
