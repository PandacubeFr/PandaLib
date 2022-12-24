package fr.pandacube.lib.paper.reflect.util;

import fr.pandacube.lib.paper.reflect.wrapper.craftbukkit.CraftServer;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility method to get the list of the primary world of the Bukkit/Paper server.
 * The primary worlds are the world that are loaded by the server at the startup, that the plugins cannot unload.
 */
public class PrimaryWorlds {

	/**
	 * An unmodifiable list containing the names of the primary worlds of this server instance, in the order they are
	 * loaded. This list can be accessed even if the corresponding worlds are not yet loaded, for instance in the
	 * {@link Plugin#onLoad()} method.
	 */
	public static final List<String> PRIMARY_WORLDS;


	static {
		List<String> primaryWorlds = new ArrayList<>(3);

		String world = ReflectWrapper.wrapTyped(Bukkit.getServer(), CraftServer.class).getServer().getLevelIdName();

		primaryWorlds.add(world);
		if (Bukkit.getAllowNether()) primaryWorlds.add(world + "_nether");
		if (Bukkit.getAllowEnd()) primaryWorlds.add(world + "_the_end");

		PRIMARY_WORLDS = Collections.unmodifiableList(primaryWorlds);
	}
	

	
}
