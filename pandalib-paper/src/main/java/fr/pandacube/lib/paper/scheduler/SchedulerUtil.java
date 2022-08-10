package fr.pandacube.lib.paper.scheduler;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import org.bukkit.Bukkit;

import fr.pandacube.lib.util.Log;
import fr.pandacube.lib.paper.PandaLibPaper;

/**
 * Provides methods to easily manage synchronous and asynchronous operations with the server thread.
 */
public class SchedulerUtil {

	/**
	 * Ensure that the provided runnable is run on the server thread.
	 * If the current thread is the server thread, then the task is run right now, then this method returns.
	 * If the current thread is another thread, it passes over the runnable to the Bukkit scheduler, then returns
	 * imediately.
	 * @param task the task to run on the main thread.
	 */
	public static void runOnServerThread(Runnable task) {
		if (Bukkit.isPrimaryThread())
			task.run();
		
		Bukkit.getScheduler().runTask(PandaLibPaper.getPlugin(), task);
	}

	/**
	 * Runs the provided task on the main thread, and wait for this task to end to return its value.
	 * If the current thread is the server thread, then the task is run right now, then this method returns with the
	 * return value of the provided task. Otherwise, it will wait for the task to run on the main thread, to be able to
	 * return the value from the task.
	 * @param task the task to run on the main thread and to get the value from.
	 * @return the value returned by the task.
	 * @param <T> the type fo the return value of the task
	 * @throws CancellationException – if the task was cancelled
	 * @throws ExecutionException – if the task threw an exception
	 * @throws InterruptedException – if the current thread was interrupted while waiting
	 */
	public static <T> T runOnServerThreadAndWait(Callable<T> task) throws Exception {
		if (Bukkit.isPrimaryThread())
			return task.call();
		
		return Bukkit.getScheduler().callSyncMethod(PandaLibPaper.getPlugin(), () -> {
			try {
				return task.call();
			} catch (Exception e) {
				Log.severe("Exception while running callback code on server Thread. The source exception is:", e);
				throw e;
			}
		}).get();
	}

	/**
	 * Runs the provided task on the main thread, and wait for this task to end to return.
	 * If the current thread is the server thread, then the task is run right now, then this method returns.
	 * Otherwise, it will wait for the task to finish on the main thread.
	 * @param task the task to run on the main thread.
	 * @throws CancellationException – if the task was cancelled
	 * @throws ExecutionException – if the task threw an exception
	 * @throws InterruptedException – if the current thread was interrupted while waiting
	 */
	public static void runOnServerThreadAndWait(Runnable task) throws Exception {
		runOnServerThreadAndWait((Callable<Void>)() -> {
			task.run();
			return null;
		});
	}
	
	
	

}
