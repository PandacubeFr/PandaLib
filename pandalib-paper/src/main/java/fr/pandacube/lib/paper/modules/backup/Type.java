package fr.pandacube.lib.paper.modules.backup;

public enum Type {
	WORLDS,
	WORKDIR;
	
	@Override
	public String toString() {
		return name().toLowerCase();
	}

	public boolean backupEnabled(BackupConfig cfg) {
		return switch (this) {
			case WORLDS -> cfg.worldBackupEnabled;
			case WORKDIR -> cfg.workdirBackupEnabled;
		};
	}
	
}
