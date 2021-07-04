package fr.pandacube.lib.paper.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.bukkit.Bukkit;

import fr.pandacube.lib.core.util.Log;
import fr.pandacube.lib.paper.PandaLibPaper;

public class ThreadUtil {
	
	public static void runOnServerThread(Runnable task) {
		if (Bukkit.isPrimaryThread())
			task.run();
		
		Bukkit.getScheduler().runTask(PandaLibPaper.getPlugin(), task);
	}
	
	public static <T> T runOnServerThreadAndWait(Callable<T> task) throws Exception {
		if (Bukkit.isPrimaryThread())
			return task.call();
		
		try {
			return Bukkit.getScheduler().callSyncMethod(PandaLibPaper.getPlugin(), task).get();
		} catch (ExecutionException e) {
			Log.severe("Execution Exception while running code on server Thread. The source exception is:",
					e.getCause());
			throw e;
		}
	}
	
	public static void runOnServerThreadAndWait(Runnable task) throws Exception {
		runOnServerThreadAndWait((Callable<Void>)() -> {
			task.run();
			return null;
		});
	}
	
	
	

}
