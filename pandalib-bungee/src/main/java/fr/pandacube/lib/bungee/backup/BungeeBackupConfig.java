package fr.pandacube.lib.bungee.backup;

import fr.pandacube.lib.core.backup.BackupCleaner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BungeeBackupConfig {
    public boolean workdirBackupEnabled = true;
    public boolean logsBackupEnabled = true;
    public String scheduling = "0 2 * * *"; // cron format, here is everyday at 2am
    public File backupDirectory = null;
    public BackupCleaner workdirBackupCleaner = BackupCleaner.KEEPING_1_EVERY_N_MONTH(3).merge(BackupCleaner.KEEPING_N_LAST(5));
    public List<String> workdirIgnoreList = new ArrayList<>();
}
