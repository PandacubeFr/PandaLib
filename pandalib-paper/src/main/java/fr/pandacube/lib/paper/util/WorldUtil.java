package fr.pandacube.lib.paper.util;

import org.bukkit.Bukkit;
import org.bukkit.World.Environment;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class WorldUtil {
	
	
	
	
	public static Environment determineEnvironment(String world) {
		if (Bukkit.getWorld(world) != null) {
			return Bukkit.getWorld(world).getEnvironment();
		}
		
		File worldFolder = worldDir(world);
		
		if (!worldFolder.isDirectory())
			throw new IllegalStateException("The world " + world + " is not a valid world (directory not found).");
		
		if (!new File(worldFolder, "level.dat").isFile())
			throw new IllegalStateException("The world " + world + " is not a valid world (level.dat not found).");
		
		if (new File(worldFolder, "region").isDirectory())
			return Environment.NORMAL;
		
		if (new File(worldFolder, "DIM-1" + File.pathSeparator + "region").isDirectory())
			return Environment.NETHER;
		
		if (new File(worldFolder, "DIM1" + File.pathSeparator + "region").isDirectory())
			return Environment.THE_END;
		
		throw new IllegalStateException("Unable to determine the type of the world " + world + ".");
	}
	
	
	
	private static final List<String> REGION_DATA_FILES = Arrays.asList("entities", "poi", "region", "DIM-1", "DIM1");
	
	public static List<File> regionDataFiles(String world) {
		return onlyExistents(worldDir(world), REGION_DATA_FILES);
	}

	public static List<File> mapFiles(String world) {
		Pattern mapFilePattern = Pattern.compile("map_\\d+.dat");
		return List.of(dataDir(world).listFiles((dir, name) -> mapFilePattern.matcher(name).find() || "idcounts.dat".equals(name)));
	}
	
	private static List<File> onlyExistents(File worldDir, List<String> searchList) {
		return searchList.stream()
				.map(f -> new File(worldDir, f))
				.filter(File::exists)
				.toList();
	}

	public static File worldDir(String world) {
		return new File(Bukkit.getWorldContainer(), world);
	}

	public static File dataDir(String world) {
		return new File(worldDir(world), "data");
	}
	
	public static boolean isValidWorld(String world) {
		File d = worldDir(world);
		return d.isDirectory() && new File(d, "level.dat").isFile(); 
	}
	
	
}
