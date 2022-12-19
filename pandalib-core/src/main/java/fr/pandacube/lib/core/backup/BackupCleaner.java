package fr.pandacube.lib.core.backup;

import fr.pandacube.lib.chat.Chat;
import fr.pandacube.lib.util.Log;
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

public abstract class BackupCleaner implements UnaryOperator<TreeSet<LocalDateTime>> {

    private static final boolean testOnly = true; // if true, no files are deleted

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


    public static BackupCleaner KEEPING_1_EVERY_N_MONTH(int n) {
        return new BackupCleaner() {
            @Override
            public TreeSet<LocalDateTime> apply(TreeSet<LocalDateTime> localDateTimes) {
                return localDateTimes.stream()
                        .collect(Collectors.groupingBy(
                                ldt -> {
                                    return ldt.getYear() * 4 + ldt.getMonthValue() / n;
                                },
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




    public void cleanupArchives(File archiveDir, String compressDisplayName) {
        String[] files = archiveDir.list();

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
