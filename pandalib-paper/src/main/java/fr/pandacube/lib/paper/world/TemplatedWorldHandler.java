package fr.pandacube.lib.paper.world;

import fr.pandacube.lib.util.FileUtils;
import fr.pandacube.lib.util.RandomUtil;
import fr.pandacube.lib.util.log.Log;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Handles worlds that are loaded temporarily and based on a template. Useful for mini-game servers.
 */
public class TemplatedWorldHandler implements Listener {
	
	private static final Map<String, World> loadedWorlds = new HashMap<>();


	/**
	 * Gets a world based on the provided template world.
	 * <p>
	 * If a world based on the template is already loaded, it will be returned.
	 * If there is no world loaded based on the template, the template world will be copied with a temporary name, and
	 * loaded like a regular world with {@link Bukkit#createWorld(WorldCreator)}.
	 * Only one copy per template can be loaded per server instance.
	 * @param templateWorld the template name of the world, that is the name of the original world's folder.
	 * @param operationOnLoad an optional consumer executed if a world is loaded (it is ignored if a copy of the template is already loaded).
	 * @return a World instance based on a copy of the provided world.
	 * @throws IOException if an error occurs while loading a world.
	 */
	public static World getOrLoadWorld(String templateWorld, Consumer<World> operationOnLoad) throws IOException {
		if (loadedWorlds.containsKey(templateWorld)) {
			return loadedWorlds.get(templateWorld);
		}
		try {
			return loadGameWorld(templateWorld, operationOnLoad);
		} catch (IllegalStateException e) {
			Log.severe(e);
			return null;
		}
	}


	/**
	 * Gets the already-loaded world based on the provided template world.
	 * @param templateWorld the template name of the world, that is the name of the original world's folder.
	 * @return a World instance based on a copy of the provided world, or null if there is none loaded yet.
	 */
	public static World getWorldIfLoaded(String templateWorld) {
		if (loadedWorlds.containsKey(templateWorld)) {
			return loadedWorlds.get(templateWorld);
		}
		return null;
	}


	/**
	 * Unload the world based on the provided template world.
	 * Do nothing if there is no loaded wold based on the provided template.
	 * After unloading using {@link Bukkit#unloadWorld(World, boolean)}, the copy of the template is deleted.
	 * @param templateWorld the template name of the world, that is the name of the original world's folder.
	 * @return true if the world was unloaded successfully (or there were no world to unload), false if the unloading
	 * failed ({@link Bukkit#unloadWorld(World, boolean)} returned false).
	 */
	public static boolean unloadWorld(String templateWorld) {
		if (loadedWorlds.containsKey(templateWorld)) {
			World rem = loadedWorlds.remove(templateWorld);
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


	/**
	 * Unload all the templated worlds, using {@link #unloadWorld(String)}.
	 */
	public static void unloadUnusedWorlds() {
		for (String world : new ArrayList<>(loadedWorlds.keySet())) {
			World rem = loadedWorlds.get(world);
			if (rem.getPlayers().stream().noneMatch(Player::isOnline)) {
				unloadWorld(world);
			}
		}
	}


	/**
	 * Tells if the provided template world has a currently loaded copy.
	 * @param templateWorld the template name of the world, that is the name of the original world's folder.
	 * @return true if the world is loaded.
	 */
	public static boolean isWorldLoaded(String templateWorld) {
		return loadedWorlds.containsKey(templateWorld);
	}
	
	
	
	private static World loadGameWorld(String world, Consumer<World> operationOnLoad) throws IOException {
		if (loadedWorlds.containsKey(world))
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
		loadedWorlds.put(world, w);
		if (Bukkit.getPluginManager().getPlugin("Multiverse-Core") != null)
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvm set hidden true "+copiedName);
		operationOnLoad.accept(w);
		return w;
	}
	
	
	
	private TemplatedWorldHandler() {}
	
}
