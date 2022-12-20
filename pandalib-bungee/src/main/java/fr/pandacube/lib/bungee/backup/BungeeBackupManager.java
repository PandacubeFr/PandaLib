package fr.pandacube.lib.bungee.backup;

import fr.pandacube.lib.core.backup.BackupManager;
import fr.pandacube.lib.core.backup.BackupProcess;
import fr.pandacube.lib.core.backup.RotatedLogsBackupProcess;

import java.io.File;

public class BungeeBackupManager extends BackupManager {

	BungeeBackupConfig config;
	
	public BungeeBackupManager(BungeeBackupConfig config) {
		super(config.backupDirectory);
		setConfig(config);

		addProcess(new BungeeWorkdirProcess(this));
		addProcess(new RotatedLogsBackupProcess(this, false, new File("logs"), "[0-9]{4}-[0-9]{2}-[0-9]{2}(-[0-9]+)?\\.log\\.gz"));
	}

	@Override
	protected void addProcess(BackupProcess process) {
		updateProcessConfig(process);
		super.addProcess(process);
	}

	public void setConfig(BungeeBackupConfig config) {
		this.config = config;
		backupQueue.forEach(this::updateProcessConfig);
	}


	public void updateProcessConfig(BackupProcess process) {
		if (process instanceof BungeeWorkdirProcess) {
			process.setEnabled(config.workdirBackupEnabled);
			process.setBackupCleaner(config.workdirBackupCleaner);
			process.setScheduling(config.scheduling);
			process.setIgnoreList(config.workdirIgnoreList);
		}
		else if (process instanceof RotatedLogsBackupProcess) {
			process.setEnabled(config.logsBackupEnabled);
		}
	}


}
