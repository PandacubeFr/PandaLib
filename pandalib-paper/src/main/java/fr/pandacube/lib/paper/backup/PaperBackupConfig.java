package fr.pandacube.lib.paper.backup;

import fr.pandacube.lib.core.backup.BackupCleaner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A basic class holding configuration for {@link PaperBackupManager}.
 */
@SuppressWarnings("CanBeFinal")
public class PaperBackupConfig {

    /**
     * Creates a new Paper backup config.
     */
    public PaperBackupConfig() {}

    /**
     * Set to true to enable worlds backup.
     * Defaults to true.
     */
    public boolean worldBackupEnabled = true;

    /**
     * Set to true to enable the backup of the working directory.
     * The workdir backup will already ignore the logs directory and any world folder (folder with a level.dat file in it).
     * Defaults to true.
     */
    public boolean workdirBackupEnabled = true;

    /**
     * Set to true to enable the backup of logs.
     * Defaults to true.
     */
    public boolean logsBackupEnabled = true;

    /**
     * The cron-formatted scheduling of the worlds and workdir backups.
     * The default value is {@code "0 2 * * *"}, that is every day at 2am.
     */
    public String scheduling = "0 2 * * *"; // cron format, here is every day at 2am

    /**
     * The backup target directory.
     * Must be set (defaults to null).
     */
    public File backupDirectory = null;

    /**
     * The backup cleaner for the worlds backup.
     * Defaults to keep 1 backup every 3 month + the last 5 backups.
     */
    public BackupCleaner worldBackupCleaner = BackupCleaner.KEEPING_1_EVERY_N_MONTH(3).merge(BackupCleaner.KEEPING_N_LAST(5));

    /**
     * The backup cleaner for the workdir backup.
     * Defaults to keep 1 backup every 3 month + the last 5 backups.
     */
    public BackupCleaner workdirBackupCleaner = BackupCleaner.KEEPING_1_EVERY_N_MONTH(3).merge(BackupCleaner.KEEPING_N_LAST(5));

    /**
     * The list of files or directory to ignore.
     * Defaults to none.
     * The workdir backup will already ignore the logs directory and any world folder (folder with a level.dat file in it).
     */
    public List<String> workdirIgnoreList = new ArrayList<>();

}
