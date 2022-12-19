package fr.pandacube.lib.core.backup;

import fc.cron.CronExpression;
import fr.pandacube.lib.util.Log;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.LongStream;

public class BackupManager extends TimerTask {

    private final File backupDirectory;

    protected final Persist persist;

    protected final List<BackupProcess> backupQueue = new ArrayList<>();

    /* package */ final AtomicReference<BackupProcess> runningBackup = new AtomicReference<>();

    private final Timer schedulerTimer = new Timer();

    public BackupManager(File backupDirectory) {
        this.backupDirectory = backupDirectory;
        persist = new Persist(this);


        long nextMinute = ZonedDateTime.now().plusMinutes(1).withSecond(0).withNano(0)
                .toInstant().toEpochMilli();
        schedulerTimer.scheduleAtFixedRate(this, new Date(nextMinute), 60_000);
    }


    protected void addProcess(BackupProcess process) {
        process.displayNextSchedule();
        backupQueue.add(process);
    }


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

        persist.save();
    }










}
