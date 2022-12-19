package fr.pandacube.lib.core.backup;

import com.google.common.io.Files;
import fr.pandacube.lib.util.Log;
import net.md_5.bungee.api.ChatColor;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiPredicate;

public class RotatedLogsBackupProcess extends BackupProcess {
    final String logFileRegexPattern;
    final File sourceLogDirectory;
    final boolean inNewThread;

    public RotatedLogsBackupProcess(BackupManager bm, boolean inNewThread, File sourceLogDir, String logFileRegexPattern) {
        super(bm, "logs");
        this.logFileRegexPattern = logFileRegexPattern;
        sourceLogDirectory = sourceLogDir;
        this.inNewThread = inNewThread;
        super.setScheduling("0 1 * * *"); // do this every day at 1 am, by default
    }

    @Override
    public void run() {
        // do not call super. We override the zip archive process, we just want to copy log files, here
        if (inNewThread) {
            new Thread(this::actuallyRun, "Backup Thread " + identifier).start();
        }
        else {
            actuallyRun();
        }

    }


    private void actuallyRun() {

        Log.info("[Backup] Starting for " + ChatColor.GRAY + getDisplayName() + ChatColor.RESET + " ...");

        try {
            // wait a little after the log message above, in case the log file rotation has to be performed.
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        onBackupStart();

        boolean success = false;

        File targetDir = getTargetDir();

        try {
            List<File> filesToMove = getFilesToMove();

            for (File source : filesToMove) {
                try {
                    Files.move(source, new File(targetDir, source.getName()));
                } catch (IOException e) {
                    Log.severe("Unable to move file " + source + " into " + targetDir);
                }
            }

            success = true;

            Log.info("[Backup] Finished for " + ChatColor.GRAY + getDisplayName() + ChatColor.RESET);
        } catch (final Exception e) {
            Log.severe("[Backup] Failed for : " + ChatColor.GRAY + getDisplayName() + ChatColor.RESET, e);
        } finally {
            onBackupEnd(success);

            displayNextSchedule();
        }
    }



    public List<File> getFilesToMove() {
        List<File> ret = new ArrayList<>();
        for (File f : getSourceDir().listFiles()) {
            if (f.getName().matches(logFileRegexPattern))
                ret.add(f);
        }
        return ret;
    }





    @Override
    public BiPredicate<File, String> getFilenameFilter() {
        return null;
    }

    @Override
    public File getSourceDir() {
        return sourceLogDirectory;
    }

    @Override
    protected File getTargetDir() {
        return new File(getBackupManager().getBackupDirectory(), "logs");
    }

    @Override
    protected void onBackupStart() {
    }

    @Override
    protected void onBackupEnd(boolean success) {
        setDirtySinceNow();
    }

    @Override
    public void displayNextSchedule() {
        Log.info("[Backup] " + ChatColor.GRAY + getDisplayName() + ChatColor.RESET + " next backup on "
                + DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date(getNext())));
    }
}
