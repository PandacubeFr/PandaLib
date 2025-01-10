package fr.pandacube.lib.paper.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Provides utility methods to manage and analyze world's directory.
 */
public class WorldUtil {


	/**
	 * Determine the {@link Environment} of the provided world.
	 * @param world the name of the world.
	 * @return the {@link Environment}.
	 * @throws IllegalStateException if the provided world is not valid (cannot determine its environment)
	 */
	public static Environment determineEnvironment(String world) {
		World bWorld = Bukkit.getWorld(world);
		if (bWorld != null) {
			return bWorld.getEnvironment();
		}
		
		File worldFolder = worldDir(world);
		
		if (!worldFolder.isDirectory())
			throw new IllegalStateException("The world " + world + " is not a valid world (directory not found).");
		
		if (!new File(worldFolder, "level.dat").isFile())
			throw new IllegalStateException("The world " + world + " is not a valid world (level.dat not found).");
		
		if (new File(worldFolder, "region").isDirectory())
			return Environment.NORMAL;
		
		if (new File(worldFolder, "DIM-1" + File.separator + "region").isDirectory())
			return Environment.NETHER;
		
		if (new File(worldFolder, "DIM1" + File.separator + "region").isDirectory())
			return Environment.THE_END;
		
		throw new IllegalStateException("Unable to determine the type of the world " + world + ".");
	}

	/**
	 * Gets the list of all the regions of the provided world, based on the name of the region files.
	 * @param world the world.
	 * @return a {@link List} of {@link RegionCoord}.
	 */
	public static List<RegionCoord> getExistingRegions(String world) {
		Environment env = determineEnvironment(world);

		File worldFolder = worldDir(world);

		File file = switch (env) {
			case NORMAL -> new File(worldFolder, "region");
			case NETHER -> new File(worldFolder, "DIM-1" + File.separator + "region");
			case THE_END -> new File(worldFolder, "DIM1" + File.separator + "region");
            case CUSTOM -> throw new IllegalStateException("The provided world '" + world + "' has a custom Environment type. Unable to tell where the region are stored.");
        };

		String[] fileList = file.list();
		if (fileList == null) {
			throw new IllegalStateException("The provided world '" + world + "' does not have a valid region folder (expected '" + file + "').");
		}

		return Arrays.stream(fileList)
				.map(name -> {
					try {
						return RegionCoord.fromFileName(name);
					} catch (IllegalArgumentException e) {
						return null;
					}
				})
				.filter(Objects::nonNull)
				.toList();
	}
	
	private static final List<String> REGION_DATA_FILES = Arrays.asList("entities", "poi", "region", "DIM-1", "DIM1");

	/**
	 * Gets all the directory containing region related data (entities, poi, region, DIM*) of the provided world.
	 * @param world the world.
	 * @return a {@link List} of directory.
	 */
	public static List<File> regionDataFolders(String world) {
		return onlyExisting(worldDir(world), REGION_DATA_FILES);
	}

	/**
	 * Gets the list of all map related data files.
	 * That is the file {@code data/idcounts.dat} and all the files with the pattern {@code data/map_*.dat}.
	 * @param world the world.
	 * @return the list of all map related data files
	 */
	public static List<File> mapFiles(String world) {
		Pattern mapFilePattern = Pattern.compile("map_\\d+.dat");
		return List.of(dataDir(world).listFiles((dir, name) -> mapFilePattern.matcher(name).find() || "idcounts.dat".equals(name)));
	}
	
	private static List<File> onlyExisting(File worldDir, List<String> searchList) {
		return searchList.stream()
				.map(f -> new File(worldDir, f))
				.filter(File::exists)
				.toList();
	}

	/**
	 * Gets the directory of the provided world.
	 * @param world the world.
	 * @return the directory of the world.
	 */
	public static File worldDir(String world) {
		return new File(Bukkit.getWorldContainer(), world);
	}

	/**
	 * Gets the data directory of the provided world.
	 * @param world the world.
	 * @return the data directory of the world.
	 */
	public static File dataDir(String world) {
		return new File(worldDir(world), "data");
	}

	/**
	 * Tells if the provided world is a valid one (has a directory and contains a file named level.dat).
	 * @param world the world.
	 * @return true if the world exists, false otherwise.
	 */
	public static boolean isValidWorld(String world) {
		File d = worldDir(world);
		return d.isDirectory() && new File(d, "level.dat").isFile(); 
	}


	private WorldUtil() {}
	
}
