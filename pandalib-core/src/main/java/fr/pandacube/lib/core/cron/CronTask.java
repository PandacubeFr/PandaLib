package fr.pandacube.lib.core.cron;

import fc.cron.CronExpression;

/* package */ class CronTask {
    /**
     * The id of the task, used to persist its last run.
     */
    /* package */ final String taskId;
    /**
     * The task to run.
     */
    /* package */ final Runnable task;
    /**
     * The cron expression telling when to run the task.
     */
    /* package */ final CronExpression scheduling;
    /**
     * Millis timestamp of the previous run. Must be saved.
     */
    /* package */ long lastRun;
    /**
     * Millis timestamp of the next run.
     */
    /* package */ long nextRun;



    /* package */ CronTask(String taskId, Runnable task, CronExpression scheduling, long lastRun) {
        this.taskId = taskId;
        this.task = task;
        this.scheduling = scheduling;
        this.lastRun = lastRun;
        updateNextRun();
    }



    /* package */ void updateNextRun() {
        nextRun = CronScheduler.getNextTime(scheduling, lastRun);
    }


    /* package */ void runAsync() {
        Thread t = new Thread(task, "Pandalib CronTask " + taskId);
        t.start();
        lastRun = nextRun;
        updateNextRun();
    }






}
