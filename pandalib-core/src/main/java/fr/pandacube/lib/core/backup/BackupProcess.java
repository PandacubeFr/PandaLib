package fr.pandacube.lib.core.backup;

import fc.cron.CronExpression;
import fr.pandacube.lib.util.FileUtils;
import fr.pandacube.lib.util.Log;
import net.md_5.bungee.api.ChatColor;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public abstract class BackupProcess implements Comparable<BackupProcess>, Runnable {
    private final BackupManager backupManager;

    public final String identifier;


    protected ZipCompressor compressor = null;


    private boolean enabled = true;
    private String scheduling = "0 2 * * *"; // cron format, here is everyday at 2am
    private BackupCleaner backupCleaner = null;
    private List<String> ignoreList = new ArrayList<>();


    protected BackupProcess(BackupManager bm, final String n) {
        backupManager = bm;
        identifier = n;
    }

    public BackupManager getBackupManager() {
        return backupManager;
    }

    public String getIdentifier() {
        return identifier;
    }

    protected String getDisplayName() {
        return getIdentifier();
    }



    @Override
    public int compareTo(final BackupProcess process) {
        return Long.compare(getNext(), process.getNext());
    }





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

    public abstract File getSourceDir();

    protected abstract File getTargetDir();

    protected abstract void onBackupStart();

    protected abstract void onBackupEnd(boolean success);






    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getScheduling() {
        return scheduling;
    }

    public void setScheduling(String scheduling) {
        this.scheduling = scheduling;
    }

    public BackupCleaner getBackupCleaner() {
        return backupCleaner;
    }

    public void setBackupCleaner(BackupCleaner backupCleaner) {
        this.backupCleaner = backupCleaner;
    }

    public List<String> getIgnoreList() {
        return ignoreList;
    }

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
                Log.warning("[Backup] Unable to compress " + ChatColor.GRAY + getDisplayName() + ChatColor.RESET + ": source directory " + sourceDir + " doesn’t exist");
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










    public abstract void displayNextSchedule();


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


    public void logProgress() {
        if (compressor == null)
            return;
        Log.info("[Backup] " + ChatColor.GRAY + getDisplayName() + ChatColor.RESET + ": " + compressor.getState().getLegacyText());
    }






    public boolean couldRunNow() {
        if (!isEnabled())
            return false;
        if (!isDirty())
            return false;
        if (getNext() > System.currentTimeMillis())
            return false;
        return true;
    }




    public long getNext() {
        if (!hasNextScheduled())
            return Long.MAX_VALUE;
        return getNextCompress(backupManager.persist.isDirtySince(identifier));
    }

    public boolean hasNextScheduled() {
        return isEnabled() && isDirty();
    }

    public boolean isDirty() {
        return backupManager.persist.isDirty(identifier);
    }

    public void setDirtySinceNow() {
        backupManager.persist.setDirtySinceNow(identifier);
    }

    public void setNotDirty() {
        backupManager.persist.setNotDirty(identifier);
    }





    /**
     * get the timestamp (in ms) of when the next compress will run, depending on since when the files to compress are dirty.
     * @param dirtySince the timestamp in ms since the files are dirty
     * @return the timestamp in ms when the next compress of the files should be run, or 0 if it is not yet scheduled
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

        return parsedScheduling.nextTimeAfter(ZonedDateTime.ofInstant(Instant.ofEpochMilli(dirtySince), ZoneId.systemDefault()))
                .toInstant()
                .toEpochMilli();
    }
}
