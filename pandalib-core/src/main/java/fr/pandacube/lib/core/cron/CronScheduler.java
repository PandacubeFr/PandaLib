package fr.pandacube.lib.core.cron;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import fc.cron.CronExpression;
import fr.pandacube.lib.core.json.Json;
import fr.pandacube.lib.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Application wide task scheduler using Cron expression.
 */
public class CronScheduler {

    private static final Object lock = new Object();

    private static final List<CronTask> tasks = new ArrayList<>();
    private static final Map<String, CronTask> tasksById = new HashMap<>();



    private static volatile boolean init = false;
    private static void init() {
        synchronized (CronScheduler.class) {
            if (init)
                return;
            init = true;
            loadLastRuns();
            Thread t = new Thread(CronScheduler::run, "Pandalib CronScheduler Thread");
            t.setDaemon(true);
            t.start();
        }
    }



    private static void run() {
        synchronized (lock) {
            for (;;) {
                long wait = 0;
                long now = System.currentTimeMillis();

                if (!tasks.isEmpty()) {
                    CronTask next = tasks.get(0);
                    if (next.nextRun <= now) {
                        next.runAsync();
                        setLastRun(next.taskId, now);
                        onTaskUpdate(false);
                        continue;
                    }
                    else {
                        wait = next.nextRun - now;
                    }
                }
                try {
                    lock.wait(wait, 0);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }


    /**
     * Schedule a task.
     * If a task with the provided taskId already exists, it will be replaced.
     * @param taskId the id of the task.
     * @param cronExpression the scheduling of the task. May use seconds (6 values) or not (5 values).
     *                       See {@link CronExpression} for the format.
     * @param task the task to run.
     */
    public static void schedule(String taskId, String cronExpression, Runnable task) {
        init();
        synchronized (lock) {
            long lastRun = getLastRun(taskId);

            CronTask existing = getTask(taskId);
            if (existing != null) {
                // replacing task
                removeTask(taskId);
            }

            CronExpression cron = new CronExpression(cronExpression, cronExpression.split("\\s+").length == 6);
            addTask(new CronTask(taskId, task, cron, lastRun));
            onTaskUpdate(true);
        }
    }

    /**
     * Cancel a scheduled task.
     * Will not stop a current execution of the task. If the task does not exists, it will not do anything.
     * @param taskId the id of the task to cancel.
     */
    public static void unSchedule(String taskId) {
        synchronized (lock) {
            CronTask existing = getTask(taskId);
            if (existing != null) {
                removeTask(taskId);
                onTaskUpdate(true);
            }
        }
    }



    private static void onTaskUpdate(boolean notify) {
        synchronized (lock) {
            tasks.sort(Comparator.comparing(t -> t.nextRun));
            if (notify) {
                Log.info("Scheduler notified.");
                lock.notify();
            }
        }
    }


    private static void addTask(CronTask nextTask) {
        synchronized (lock) {
            tasks.add(nextTask);
            tasksById.put(nextTask.taskId, nextTask);
        }
    }

    private static CronTask getTask(String taskId) {
        synchronized (lock) {
            return tasksById.get(taskId);
        }
    }

    private static void removeTask(String taskId) {
        synchronized (lock) {
            tasks.remove(tasksById.remove(taskId));
        }
    }










    private static final Map<String, Long> savedLastRun = new LinkedHashMap<>();
    private static final File lastRunFile = new File("cron-last-run.json");

    private static void saveLastRuns() {
        try (FileWriter writer = new FileWriter(lastRunFile, false)) {
            synchronized (lock) {
                Json.gsonPrettyPrinting.toJson(savedLastRun, writer);
            }
        }
        catch (JsonParseException | IOException e) {
            Log.severe("could not save " + lastRunFile, e);
        }
    }

    private static void loadLastRuns() {
        boolean loaded = false;
        try (FileReader reader = new FileReader(lastRunFile)) {
            Map<String, Long> newData = Json.gson.fromJson(reader, new TypeToken<Map<String, Long>>(){}.getType());
            if (newData != null) {
                loaded = true;
                synchronized (lock) {
                    savedLastRun.clear();
                    savedLastRun.putAll(newData);
                }
            }
        }
        catch (final IOException ignored) { }
        catch (final JsonParseException e) {
            Log.severe("cannot load " + lastRunFile, e);
        }

        if (!loaded) {
            saveLastRuns();
        }
    }

    /* package */ static void setLastRun(String taskId, long lastRun) {
        synchronized (lock) {
            savedLastRun.put(taskId, lastRun);
            saveLastRuns();
        }
    }

    private static long getLastRun(String taskId) {
        synchronized (lock) {
            return savedLastRun.getOrDefault(taskId, System.currentTimeMillis());
        }
    }








    /**
     * Tells when the next time is scheduled, according to the provided cron expression, strictly after the provided time.
     * @param expr the cron expression to use to determine the schedule time.
     * @param lastTime the start search time.
     * @return the time of the next execution of the task.
     */
    public static long getNextTime(CronExpression expr, long lastTime) {
        return expr.nextTimeAfter(ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastTime), ZoneId.systemDefault()))
                .toInstant()
                .toEpochMilli();
    }


}
