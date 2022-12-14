package fr.pandacube.lib.paper.modules.backup;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import fr.pandacube.lib.util.Log;

public abstract class BackupCleaner implements UnaryOperator<TreeSet<LocalDateTime>> {

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




    public void cleanupArchives(File archiveDir) {
        String[] files = archiveDir.list();

        Log.info("[Backup] Cleaning up backup directory " + archiveDir + "...");

        TreeMap<LocalDateTime, File> datedFiles = new TreeMap<>();

        for (String filename : files) {
            File file = new File(archiveDir, filename);
            if (!filename.matches("\\d{8}-\\d{6}\\.zip")) {
                Log.warning("[Backup] Invalid file in backup directory: " + file);
                continue;
            }

            String dateTimeStr = filename.substring(0, filename.length() - 4);
            LocalDateTime ldt;
            try {
                ldt = LocalDateTime.parse(dateTimeStr, CompressProcess.dateFileNameFormatter);
            } catch (DateTimeParseException e) {
                Log.warning("Unable to parse file name to a date-time: " + file, e);
                continue;
            }

            datedFiles.put(ldt, file);
        }

        TreeSet<LocalDateTime> keptFiles = apply(new TreeSet<>(datedFiles.keySet()));

        for (Entry<LocalDateTime, File> datedFile : datedFiles.entrySet()) {
            if (keptFiles.contains(datedFile.getKey()))
                continue;
            // datedFile.getValue().delete(); // TODO check if the filtering is ok before actually removing files
            Log.info("[Backup] Removed expired backup file " + datedFile.getValue());
        }

        Log.info("[Backup] Backup directory " + archiveDir + " cleaned.");
    }


}
