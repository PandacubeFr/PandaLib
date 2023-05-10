package fr.pandacube.lib.core.backup;

import fr.pandacube.lib.util.Log;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Handles the backup processes.
 */
public class BackupManager extends TimerTask {

    private final File backupDirectory;

    /**
     * The {@link Persist} instance of this {@link BackupManager}.
     */
    protected final Persist persist;

    /**
     * The list of backup processes that are scheduled.
     */
    protected final List<BackupProcess> backupQueue = new ArrayList<>();

    /* package */ final AtomicReference<BackupProcess> runningBackup = new AtomicReference<>();

    private final Timer schedulerTimer = new Timer();

    /**
     * Instanciate a new backup manager.
     * @param backupDirectory the root backup directory.
     */
    public BackupManager(File backupDirectory) {
        this.backupDirectory = backupDirectory;
        if (!backupDirectory.exists()) {
            backupDirectory.mkdirs();
        }
        persist = new Persist(this);


        long nextMinute = ZonedDateTime.now().plusMinutes(1).withSecond(0).withNano(0)
                .toInstant().toEpochMilli();
        schedulerTimer.scheduleAtFixedRate(this, new Date(nextMinute), 60_000);
    }

    /**
     * Add a new backup process to the queue.
     * @param process the backup process to add.
     */
    protected void addProcess(BackupProcess process) {
        process.displayNextSchedule();
        backupQueue.add(process);
    }

    /**
     * Gets the backup root directory.
     * @return the backup root directory.
     */
    public File getBackupDirectory() {
        return backupDirectory;
    }




    public synchronized void run() {
        BackupProcess tmp;
        if ((tmp = runningBackup.get()) != null) {
            tmp.logProgress();
        }
        else {
            backupQueue.sort(null);
            for (BackupProcess process : backupQueue) {
                if (System.currentTimeMillis() >= process.getNext() && process.couldRunNow()) {
                    process.run();
                    return;
                }
            }
        }
    }


    /**
     * Disables this backup manager, canceling scheduled backups.
     * It will wait for a currently running backup to finish before returning.
     */
    public synchronized void onDisable() {

        schedulerTimer.cancel();

        if (runningBackup.get() != null) {
            Log.warning("[Backup] Waiting after the end of a backup...");
            BackupProcess tmp;
            while ((tmp = runningBackup.get()) != null) {
                try {
                    tmp.logProgress();
                    // wait 5 seconds between each progress log
                    // but check if the process has ended each .5 seconds
                    for (int i = 0; i < 10; i++) {
                        if (runningBackup.get() == null)
                            break;
                        Thread.sleep(500);
                    }
                } catch (Throwable e) { // could occur because of synchronization errors/interruption/...
                    break;
                }
            }
        }
    }










}
