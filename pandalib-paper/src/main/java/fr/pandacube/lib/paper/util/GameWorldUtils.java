package fr.pandacube.lib.paper.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import fr.pandacube.lib.util.BiMap;
import fr.pandacube.lib.util.FileUtils;
import fr.pandacube.lib.util.log.Log;
import fr.pandacube.lib.util.RandomUtil;

public class GameWorldUtils implements Listener {
	
	private static final BiMap<String, World> gameWorld = new BiMap<>();
	
	
	public static World getOrLoadGameWorld(String world, Consumer<World> operationOnLoad) throws IOException {
		if (gameWorld.containsKey(world)) {
			return gameWorld.get(world);
		}
		try {
			return loadGameWorld(world, operationOnLoad);
		} catch (IllegalStateException e) {
			Log.severe(e);
			return null;
		}
	}
	
	
	
	
	public static World getGameWorldIfLoaded(String world) {
		if (gameWorld.containsKey(world)) {
			return gameWorld.get(world);
		}
		return null;
	}
	
	
	
	
	public static boolean unloadGameWorld(String world) {
		if (gameWorld.containsKey(world)) {
			World rem = gameWorld.remove(world);
			String copiedName = rem.getName();
			boolean ret = Bukkit.unloadWorld(rem, false);
			if (ret)
				FileUtils.delete(new File(Bukkit.getWorldContainer(), copiedName));
			else
				Log.warning("Unable to unload game world " + copiedName + " for some reason.");
			return ret;
		}
		return true;
	}



	public static void unloadUnusedGameWorlds() {
		for (String world : new ArrayList<>(gameWorld.keySet())) {
			World rem = gameWorld.get(world);
			if (rem.getPlayers().stream().noneMatch(Player::isOnline)) {
				unloadGameWorld(world);
			}
		}
	}
	
	
	
	
	public static boolean isGameWorldLoaded(String world) {
		return gameWorld.containsKey(world);
	}
	
	
	
	private static World loadGameWorld(String world, Consumer<World> operationOnLoad) throws IOException {
		if (gameWorld.containsKey(world))
			throw new IllegalStateException("GameWorld '"+world+"' is already loaded.");
		
		if (!new File(Bukkit.getWorldContainer(), world).isDirectory())
			throw new IllegalStateException("GameWorld '"+world+"' does not exist");
		
		String copiedName = world + "_gen" + RandomUtil.rand.nextInt(100000, 999999);
		
		File srcDir = new File(Bukkit.getWorldContainer(), world);
		File destDir = new File(Bukkit.getWorldContainer(), copiedName);
		FileUtils.delete(destDir);
		FileUtils.copy(srcDir, destDir);
		new File(destDir, "session.lock").delete();
		new File(destDir, "uid.dat").delete();
		
		World w = Bukkit.createWorld(new WorldCreator(copiedName).environment(Environment.NORMAL));
		if (w == null) {
			throw new RuntimeException("Unable to create the world " + copiedName + ": Bukkit.createWorld(...) returned null value.");
		}
		w.setAutoSave(false);
		gameWorld.put(world, w);
		if (Bukkit.getPluginManager().getPlugin("Multiverse-Core") != null)
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvm set hidden true "+copiedName);
		operationOnLoad.accept(w);
		return w;
	}
	
	
	
	
	
}
