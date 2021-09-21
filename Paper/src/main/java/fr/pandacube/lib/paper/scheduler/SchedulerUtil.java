package fr.pandacube.lib.paper.scheduler;

import java.util.concurrent.Callable;

import org.bukkit.Bukkit;

import fr.pandacube.lib.core.util.Log;
import fr.pandacube.lib.paper.PandaLibPaper;

public class SchedulerUtil {
	
	public static void runOnServerThread(Runnable task) {
		if (Bukkit.isPrimaryThread())
			task.run();
		
		Bukkit.getScheduler().runTask(PandaLibPaper.getPlugin(), task);
	}
	
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
	
	public static void runOnServerThreadAndWait(Runnable task) throws Exception {
		runOnServerThreadAndWait((Callable<Void>)() -> {
			task.run();
			return null;
		});
	}
	
	
	

}
