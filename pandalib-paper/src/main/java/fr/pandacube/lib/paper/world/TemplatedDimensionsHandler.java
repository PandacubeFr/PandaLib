package fr.pandacube.lib.paper.world;

import fr.pandacube.lib.util.FileUtil;
import fr.pandacube.lib.util.RandomUtil;
import fr.pandacube.lib.util.log.Log;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
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
 * Handles dimensions that are loaded temporarily and based on a template. Useful for mini-game servers.
 */
public class TemplatedDimensionsHandler implements Listener {
	
	private static final Map<NamespacedKey, World> loadedDimensions = new HashMap<>();


	/**
	 * Gets a world based on the provided template world.
	 * <p>
	 * If a world based on the template is already loaded, it will be returned.
	 * If there is no world loaded based on the template, the template world will be copied with a temporary name, and
	 * loaded like a regular world with {@link Bukkit#createWorld(WorldCreator)}.
	 * Only one copy per template can be loaded per server instance.
	 * @param templateLevel the level where the template dimension is located.
	 * @param templateDimension the dimension used as a template.
	 * @param operationOnLoad an optional consumer executed if a world is loaded (it is ignored if a copy of the template is already loaded).
	 * @return a World instance based on a copy of the provided world.
	 * @throws IOException if an error occurs while loading a world.
	 */
	public static World getOrLoadDimension(String templateLevel, NamespacedKey templateDimension, Consumer<World> operationOnLoad) throws IOException {
		if (loadedDimensions.containsKey(templateDimension)) {
			return loadedDimensions.get(templateDimension);
		}
		try {
			return loadGameWorld(templateLevel, templateDimension, operationOnLoad);
		} catch (IllegalStateException e) {
			Log.severe(e);
			return null;
		}
	}


	/**
	 * Gets the already-loaded world based on the provided template world.
	 * @param templateDimension the dimension used as a template.
	 * @return a World instance based on a copy of the provided world, or null if there is none loaded yet.
	 */
	public static World getDimensionIfLoaded(NamespacedKey templateDimension) {
		if (loadedDimensions.containsKey(templateDimension)) {
			return loadedDimensions.get(templateDimension);
		}
		return null;
	}


	/**
	 * Unload the world based on the provided template world.
	 * Do nothing if there is no loaded wold based on the provided template.
	 * After unloading using {@link Bukkit#unloadWorld(World, boolean)}, the copy of the template is deleted.
	 * @param templateDimension the dimension used as a template.
	 * @return true if the world was unloaded successfully (or there were no world to unload), false if the unloading
	 * failed ({@link Bukkit#unloadWorld(World, boolean)} returned false).
	 */
	public static boolean unloadDimension(NamespacedKey templateDimension) {
		if (loadedDimensions.containsKey(templateDimension)) {
			World rem = loadedDimensions.get(templateDimension);
			NamespacedKey copiedKey = rem.getKey();
			ServerDimensionDir remDir = ServerDimensionDir.ofBukkitWorld(rem);

            if (!Bukkit.unloadWorld(rem, false)) {
				Log.warning("Unable to unload game dimension " + copiedKey + " for some reason.");
				return false;
			}
			loadedDimensions.remove(templateDimension);
			FileUtil.delete(remDir.getDirectory());

			return true;
		}
		return true;
	}


	/**
	 * Unload all the templated worlds, using {@link #unloadDimension(NamespacedKey)}.
	 */
	public static void unloadUnusedDimensions() {
		for (NamespacedKey dim : new ArrayList<>(loadedDimensions.keySet())) {
			World rem = loadedDimensions.get(dim);
			if (rem.getPlayers().stream().noneMatch(Player::isOnline)) {
				unloadDimension(dim);
			}
		}
	}


	/**
	 * Tells if the provided template world has a currently loaded copy.
	 * @param templateDimension the dimension used as a template.
	 * @return true if the world is loaded.
	 */
	public static boolean isWorldLoaded(NamespacedKey templateDimension) {
		return loadedDimensions.containsKey(templateDimension);
	}
	
	
	
	private static World loadGameWorld(String templateLevel, NamespacedKey templateDimension, Consumer<World> operationOnLoad) throws IOException {
		if (loadedDimensions.containsKey(templateDimension))
			throw new IllegalStateException("GameWorld "+templateDimension+" is already loaded.");

		DimensionDir templateDim = LevelDir.named(templateLevel).getDimension(templateDimension);
		
		if (!templateDim.isValidDimension())
			throw new IllegalStateException("Dimension "+templateDim+" is not a valid dimension.");
		
		NamespacedKey copiedKey = NamespacedKey.fromString(templateDimension.namespace() + ":" + templateDimension.value() + "_gen" + RandomUtil.rand.nextInt(100000, 999999));

		ServerDimensionDir targetDim = ServerDimensionDir.fromServerLevel(copiedKey);

		File srcDir = templateDim.getDirectory();
		File destDir = targetDim.getDirectory();

		FileUtil.delete(destDir);
		FileUtil.copy(srcDir, destDir);

		new File(destDir, "data/paper/metadata.dat").delete(); // contains the dimension’s UUID
		
		World w = Bukkit.createWorld(new WorldCreator(copiedKey).environment(Environment.NORMAL));
		if (w == null) {
			throw new RuntimeException("Unable to create the dimension " + copiedKey + ": Bukkit.createWorld(...) returned null value.");
		}
		w.setAutoSave(false);
		loadedDimensions.put(templateDimension, w);
		if (Bukkit.getPluginManager().getPlugin("Multiverse-Core") != null)
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvm set hidden true "+copiedKey);
		operationOnLoad.accept(w);
		return w;
	}
	
	
	
	private TemplatedDimensionsHandler() {}
	
}
