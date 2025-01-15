package fr.pandacube.lib.bungee.backup;

import fr.pandacube.lib.core.backup.BackupCleaner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that holds the configuration variables for {@link BungeeBackupManager}.
 */
@SuppressWarnings("CanBeFinal")
public class BungeeBackupConfig {
    /**
     * Tells if the working directory of the current bungee instance should be backed up.
     */
    public boolean workdirBackupEnabled = true;
    /**
     * Tells if the old logs of the current bungee instance should be backed up.
     */
    public boolean logsBackupEnabled = true;
    /**
     * The cron scheduling of when the workdir backup occurs.
     */
    public String scheduling = "0 2 * * *"; // cron format, here is every day at 2am
    /**
     * The destination directory for the backups.
     */
    public File backupDirectory = null;
    /**
     * The configuration handling the cleaning of the backup directory.
     */
    public BackupCleaner workdirBackupCleaner = BackupCleaner.KEEPING_1_EVERY_N_MONTH(3).merge(BackupCleaner.KEEPING_N_LAST(5));
    /**
     * A list of ignored files or directory in the workdir to exclude from the backup.
     */
    public List<String> workdirIgnoreList = new ArrayList<>();

    /**
     * Creates a new {@link BungeeBackupConfig}.
     */
    public BungeeBackupConfig() {

    }
}
