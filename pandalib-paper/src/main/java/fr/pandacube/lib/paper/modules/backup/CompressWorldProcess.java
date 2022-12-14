package fr.pandacube.lib.paper.modules.backup;

import fr.pandacube.lib.paper.util.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.function.BiPredicate;

public class CompressWorldProcess extends CompressProcess {
	
	private boolean autoSave = true; 
	
	protected CompressWorldProcess(BackupManager bm, final String n) {
		super(bm, Type.WORLDS, n);
	}
	
	private World getWorld() {
		return Bukkit.getWorld(name);
	}
	
	
	public BiPredicate<File, String> getFilenameFilter() {
		return (f, s) -> true;
	}
	
	
	@Override
	public File getSourceDir() {
		return WorldUtil.worldDir(name);
	}
	
	@Override
	protected void onCompressStart() {
		World w = getWorld();
		if (w == null)
			return;
		autoSave = w.isAutoSave();
		w.setAutoSave(false);
	}
	
	@Override
	protected void onCompressEnd(boolean success) {
		World w = getWorld();
		if (w == null)
			return;
		w.setAutoSave(autoSave);
	}

	@Override
	protected File getTargetDir() {
		return new File(backupManager.config.backupDirectory, type + "/" + name);
	}

	@Override
	protected String getDisplayName() {
		return type + "/" + name;
	}
}
