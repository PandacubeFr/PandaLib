package fr.pandacube.lib.core.backup;

import fc.cron.CronExpression;
import fr.pandacube.lib.core.cron.CronScheduler;
import fr.pandacube.lib.util.FileUtils;
import fr.pandacube.lib.util.log.Log;
import net.md_5.bungee.api.ChatColor;

import java.io.File;
import java.text.DateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * A backup process.
 */
public abstract class BackupProcess implements Comparable<BackupProcess>, Runnable {
    private final BackupManager backupManager;

    /**
     * The process identifier.
     */
    public final String identifier;

    /**
     * The zip compressor.
     */
    protected ZipCompressor compressor = null;


    private boolean enabled = true;
    private String scheduling = "0 2 * * *"; // cron format, here is every day at 2am
    private BackupCleaner backupCleaner = null;
    private List<String> ignoreList = new ArrayList<>();

    /**
     * Instantiates a new backup process.
     * @param bm the associated backup manager.
     * @param n the process identifier.
     */
    protected BackupProcess(BackupManager bm, final String n) {
        backupManager = bm;
        identifier = n;
    }

    /**
     * Gets the associated backup manager.
     * @return the associated backup manager.
     */
    public BackupManager getBackupManager() {
        return backupManager;
    }

    /**
     * Gets the process identifier.
     * @return the process identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Gets the display name of this process.
     * Default implementation returns {@link #getIdentifier()}.
     * @return the display name of this process.
     */
    protected String getDisplayName() {
        return getIdentifier();
    }



    @Override
    public int compareTo(final BackupProcess process) {
        return Long.compare(getNext(), process.getNext());
    }


    /**
     * Provides a predicate that tells if a provided file must be included in the archive or not.
     * The default implementation returns a filter based on the content of {@link #getIgnoreList()}.
     * @return a predicate.
     */
    public BiPredicate<File, String> getFilenameFilter() {
        return (file, path) -> {
            for (String exclude : ignoreList) {
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
        };
    }

    /**
     * Gets the source directory to back up.
     * @return the source directory to back up.
     */
    public abstract File getSourceDir();

    /**
     * Gets the directory in which to put the archives.
     * @return the directory in which to put the archives.
     */
    protected abstract File getTargetDir();

    /**
     * Called when the backup starts.
     */
    protected abstract void onBackupStart();

    /**
     * Called when the backup ends.
     * @param success true if the backup ended successfully.
     */
    protected abstract void onBackupEnd(boolean success);


    /**
     * Tells if this backup process is enabled.
     * A disabled backup process will not run.
     * @return true if this backup process is enabled, false otherwise.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled status of this backup process.
     * @param enabled the enabled status of this backup process.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the string representation of the scheduling, using cron format.
     * @return the string representation of the scheduling.
     */
    public String getScheduling() {
        return scheduling;
    }

    /**
     * Sets the string representation of the scheduling.
     * @param scheduling the string representation of the scheduling, in the CRON format (without seconds).
     */
    public void setScheduling(String scheduling) {
        this.scheduling = scheduling;
    }

    /**
     * Gets the associated backup cleaner, that is executed at the end of this backup process.
     * @return the associated backup cleaner.
     */
    public BackupCleaner getBackupCleaner() {
        return backupCleaner;
    }

    /**
     * Sets the backup cleaner of this backup process.
     * @param backupCleaner the backup cleaner of this backup process.
     */
    public void setBackupCleaner(BackupCleaner backupCleaner) {
        this.backupCleaner = backupCleaner;
    }

    /**
     * Gets the current list of files that are ignored during the backup process.
     * @return the current list of files that are ignored during the backup process.
     */
    public List<String> getIgnoreList() {
        return ignoreList;
    }

    /**
     * Sets a new list of files that will be ignored during the backup process.
     * @param ignoreList the new list of files that are ignored during the backup process.
     */
    public void setIgnoreList(List<String> ignoreList) {
        this.ignoreList = ignoreList;
    }








    @Override
    public void run() {
        getBackupManager().runningBackup.set(this);

        try {
            BiPredicate<File, String> filter = getFilenameFilter();
            File sourceDir = getSourceDir();

            if (!sourceDir.exists()) {
                Log.warning("[Backup] Unable to compress " + ChatColor.GRAY + getDisplayName() + ChatColor.RESET + ": source directory " + sourceDir + " doesn't exist");
                return;
            }

            File targetDir = getTargetDir();
            File target = new File(targetDir, getDateFileName() + ".zip");

            onBackupStart();

            new Thread(() -> {
                Log.info("[Backup] Starting for " + ChatColor.GRAY + getDisplayName() + ChatColor.RESET + " ...");

                compressor = new ZipCompressor(sourceDir, target, 9, filter);

                boolean success = false;
                try {
                    compressor.compress();

                    success = true;

                    Log.info("[Backup] Finished for " + ChatColor.GRAY + getDisplayName() + ChatColor.RESET);

                    try {
                        BackupCleaner cleaner = getBackupCleaner();
                        if (cleaner != null)
                            cleaner.cleanupArchives(targetDir, getDisplayName());
                    } catch (Exception e) {
                        Log.severe(e);
                    }
                }
                catch (final Exception e) {
                    Log.severe("[Backup] Failed: " + sourceDir + " -> " + target, e);

                    FileUtils.delete(target);
                    if (target.exists())
                        Log.warning("unable to delete: " + target);
                } finally {

                    getBackupManager().runningBackup.set(null);

                    onBackupEnd(success);

                    displayNextSchedule();

                }
            }, "Backup Thread " + identifier).start();
        } catch (Throwable t) {
            getBackupManager().runningBackup.set(null);
            throw t;
        }

    }


    /**
     * Logs the scheduling status of this backup process.
     */
    public void displayNextSchedule() {
        Log.info("[Backup] " + ChatColor.GRAY + getDisplayName() + ChatColor.RESET + " next backup on "
                + DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date(getNext())));
    }


    /**
     * A formatter used to format and parse the name of backup archives, based on a date and time.
     */
    public static final DateTimeFormatter dateFileNameFormatter = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.YEAR, 4)
            .appendValue(ChronoField.MONTH_OF_YEAR, 2)
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .appendLiteral('-')
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .toFormatter();


    private String getDateFileName() {
        return dateFileNameFormatter.format(ZonedDateTime.now());
    }

    /**
     * Logs the progress of this currently running backup process.
     * Logs nothing if this backup is not in progress.
     */
    public void logProgress() {
        if (compressor == null)
            return;
        Log.info("[Backup] " + ChatColor.GRAY + getDisplayName() + ChatColor.RESET + ": " + compressor.getState().getLegacyText());
    }


    /**
     * Tells if this backup process could start now.
     * @return true if this backup process could start now, false otherwise.
     */
    public boolean couldRunNow() {
        if (!isEnabled())
            return false;
        if (!isDirty())
            return false;
        return getNext() <= System.currentTimeMillis();
    }


    /**
     * Gets the time of the next scheduled run.
     * @return the time, in millis-timestamp, of the next scheduled run, or {@link Long#MAX_VALUE} if it’s not scheduled.
     */
    public long getNext() {
        if (!hasNextScheduled())
            return Long.MAX_VALUE;
        return getNextCompress(backupManager.persist.isDirtySince(identifier));
    }

    /**
     * Tells if this backup is scheduled or not.
     * @return true if this backup is scheduled, false otherwise.
     */
    public boolean hasNextScheduled() {
        return isEnabled() && isDirty();
    }

    /**
     * Tells if the content to be backed up is dirty or not. The source data is not dirty if it has not changed since
     * the last backup.
     * @return the dirty status of the data to be backed-up by this backup process.
     */
    public boolean isDirty() {
        return backupManager.persist.isDirty(identifier);
    }

    /**
     * Sets the source data as dirty since now.
     */
    public void setDirtySinceNow() {
        backupManager.persist.setDirtySinceNow(identifier);
    }

    /**
     * Sets the source data as not dirty.
     */
    public void setNotDirty() {
        backupManager.persist.setNotDirty(identifier);
    }





    /**
     * Gets the millis-timestamp of when the next compress will run, depending on since when the files to compress are dirty.
     * @param dirtySince the timestamp in ms since the files are dirty.
     * @return the timestamp in ms when the next compress of the files should be run, or 0 if it is not yet scheduled.
     */
    public long getNextCompress(long dirtySince) {
        if (dirtySince == -1)
            return 0;

        CronExpression parsedScheduling;
        try {
            parsedScheduling = new CronExpression(getScheduling(), false);
        } catch (IllegalArgumentException e) {
            Log.severe("Invalid backup scheduling configuration '" + getScheduling() + "'.", e);
            return 0;
        }

        return CronScheduler.getNextTime(parsedScheduling, dirtySince);
    }
}
