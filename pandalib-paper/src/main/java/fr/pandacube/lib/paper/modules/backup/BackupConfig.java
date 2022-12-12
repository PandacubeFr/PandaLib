package fr.pandacube.lib.paper.modules.backup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BackupConfig {
    public boolean worldBackupEnabled = true;
    public boolean workdirBackupEnabled = true;
    public String scheduling = "0 2 * * 1"; // cron format, here is everyday at 2am
    public File backupDirectory = null;
    public BackupCleaner worldBackupCleaner = BackupCleaner.KEEPING_1_EVERY_N_MONTH(3).merge(BackupCleaner.KEEPING_N_LAST(5));
    public BackupCleaner workdirBackupCleaner = BackupCleaner.KEEPING_1_EVERY_N_MONTH(3).merge(BackupCleaner.KEEPING_N_LAST(5));
    public List<String> workdirIgnoreList = new ArrayList<>();
}
