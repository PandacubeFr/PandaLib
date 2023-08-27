package fr.pandacube.lib.core.backup;

import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.util.log.Log;
import net.md_5.bungee.api.ChatColor;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static fr.pandacube.lib.chat.ChatStatic.text;

/**
 * Cleanup a backup directory (i.e. removes old backup archives).
 * It is possible to combine different instances to affect which archive to keep or delete.
 */
public abstract class BackupCleaner implements UnaryOperator<TreeSet<LocalDateTime>> {

    private static final boolean testOnly = false; // if true, no files are deleted

    /**
     * Creates a {@link BackupCleaner} that keeps the n last archives in the backup directory.
     * @param n the number of last archives to keep.
     * @return a {@link BackupCleaner} that keeps the n last archives in the backup directory.
     */
    public static BackupCleaner KEEPING_N_LAST(int n) {
        return new BackupCleaner() {
            @Override
            public TreeSet<LocalDateTime> apply(TreeSet<LocalDateTime> archives) {
                return archives.descendingSet().stream()
                        .limit(n)
                        .collect(Collectors.toCollection(TreeSet::new));
            }
        };
    }


    /**
     * Creates a {@link BackupCleaner} that keeps one archive every n month.
     * <p>
     * This cleaner divides each year into sections of n month. For each month, its compute a section id using the
     * formula <code><i>YEAR</i> * (12 / <i>n</i>) + <i>MONTH</i> / <i>n</i></code>. It then keeps the first archive
     * found in each section.
     *
     * @param n the interval in month between each kept archives. Must be a divider of 12 (1, 2, 3, 4, 6 or 12).
     * @return a {@link BackupCleaner} that keeps one archive every n month.
     */
    public static BackupCleaner KEEPING_1_EVERY_N_MONTH(int n) {
        return new BackupCleaner() {
            @Override
            public TreeSet<LocalDateTime> apply(TreeSet<LocalDateTime> localDateTimes) {
                return localDateTimes.stream()
                        .collect(Collectors.groupingBy(
                                ldt -> ldt.getYear() * (12 / n) + ldt.getMonthValue() / n,
                                TreeMap::new,
                                Collectors.minBy(LocalDateTime::compareTo))
                        )
                        .values()
                        .stream()
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toCollection(TreeSet::new));
            }
        };
    }


    /**
     * Creates a new {@link BackupCleaner} that keeps the archives kept by this {@link BackupCleaner} or by the provided
     * one.
     * In other word, it makes a union operation with the set of archives kept by both original {@link BackupCleaner}.
     * @param other the other {@link BackupCleaner} to merge with.
     * @return a new {@link BackupCleaner}. The original ones are not affected.
     */
    public BackupCleaner merge(BackupCleaner other) {
        BackupCleaner self = this;
        return new BackupCleaner() {
            @Override
            public TreeSet<LocalDateTime> apply(TreeSet<LocalDateTime> archives) {
                TreeSet<LocalDateTime> merged = new TreeSet<>();
                merged.addAll(self.apply(archives));
                merged.addAll(other.apply(archives));
                return merged;
            }
        };
    }


    /**
     * Performs the cleanup operation on the provided directory.
     * @param archiveDir the backup directory to clean up.
     * @param compressDisplayName the display name of the backup process that manages the backup directory. Used for logs.
     */
    public void cleanupArchives(File archiveDir, String compressDisplayName) {
        String[] files = archiveDir.list();
        if (files == null)
            return;

        Log.info("[Backup] Cleaning up backup directory " + ChatColor.GRAY + compressDisplayName + ChatColor.RESET + "...");

        TreeMap<LocalDateTime, File> datedFiles = new TreeMap<>();

        for (String filename : files) {
            File file = new File(archiveDir, filename);
            if (!filename.matches("\\d{8}-\\d{6}\\.zip")) {
                Log.warning("[Backup] " + ChatColor.GRAY + compressDisplayName + ChatColor.RESET + " Invalid file in backup directory: " + filename);
                continue;
            }

            String dateTimeStr = filename.substring(0, filename.length() - 4);
            LocalDateTime ldt;
            try {
                ldt = LocalDateTime.parse(dateTimeStr, BackupProcess.dateFileNameFormatter);
            } catch (DateTimeParseException e) {
                Log.warning("[Backup] " + ChatColor.GRAY + compressDisplayName + ChatColor.RESET + " Unable to parse file name to a date-time: " + filename, e);
                continue;
            }

            datedFiles.put(ldt, file);
        }

        TreeSet<LocalDateTime> keptFiles = apply(new TreeSet<>(datedFiles.keySet()));

        Chat c = text("[Backup] ")
                .then(text(compressDisplayName).gray())
                .thenText(testOnly ? " Archive cleanup debug (no files are actually deleted):\n" : " Deleted archive files:\n");
        boolean oneDeleted = false;
        for (Entry<LocalDateTime, File> datedFile : datedFiles.entrySet()) {
            if (keptFiles.contains(datedFile.getKey())) {
                if (testOnly)
                    c.thenText("- " + datedFile.getValue().getName() + " ")
                            .thenSuccess("kept")
                            .thenText(".\n");
                continue;
            }
            oneDeleted = true;
            c.thenText("- " + datedFile.getValue().getName() + " ");
            if (testOnly)
                c.thenFailure("deleted")
                        .thenText(".\n");
            else
                datedFile.getValue().delete();
        }

        if (testOnly || oneDeleted)
            Log.warning(c.getLegacyText());

        Log.info("[Backup] Backup directory " + ChatColor.GRAY + compressDisplayName + ChatColor.RESET + " cleaned.");
    }


}
