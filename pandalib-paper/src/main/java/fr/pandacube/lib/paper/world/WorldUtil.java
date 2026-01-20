package fr.pandacube.lib.paper.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Provides utility methods to manage and analyze world's directory.
 */
public class WorldUtil {


	/**
	 * Gets the directory of the provided world.
	 * @param world the world.
	 * @return the directory of the world.
	 */
	public static File worldDir(String world) {
		return new File(Bukkit.getWorldContainer(), world);
	}

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
	 * Determines the directory containing the various region, poi, entities, data directories for the provided world,
	 * taking into account the environment.
	 * <ul>
	 *     <li>For the overworld, this is the main world directory.</li>
	 *     <li>For the nether, this is the DIM-1 directory.</li>
	 *     <li>For the end, this is the DIM1 directory.</li>
	 * </ul>
	 * @param world the name of the world.
	 * @throws IllegalStateException if the provided world is not valid (cannot determine its environment)
	 * @return the environment directory.
	 */
	public static File environmentDir(String world) {
        File worldFolder = worldDir(world);
		return switch (determineEnvironment(world)) {
			case NORMAL -> worldFolder;
			case NETHER -> new File(worldFolder, "DIM-1");
			case THE_END -> new File(worldFolder, "DIM1");
			case CUSTOM -> throw new IllegalStateException("The provided world '" + world + "' has a custom Environment type. Unable to determine the environment directory.");
		};

	}

	/**
	 * Gets the list of all the regions of the provided world, based on the name of the region files.
	 * @param world the world.
	 * @return a {@link List} of {@link RegionCoord}.
	 */
	public static List<RegionCoord> getExistingRegions(String world) {
		File regionDir = new File(environmentDir(world), "region");

		String[] fileList = regionDir.list();
		if (fileList == null) {
			throw new IllegalStateException("The provided world '" + world + "' does not have a valid region folder (expected '" + regionDir + "').");
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

	/**
	 * Gets all the directory containing region related data (entities, poi, region, DIM*) of the provided world.
	 * @param world the world.
	 * @return a {@link List} of directory.
	 */
	public static List<File> regionDataFolders(String world) {
		File envDir = environmentDir(world);
		return Stream.of("entities", "poi", "region")
				.map(f -> new File(envDir, f))
				.filter(File::exists)
				.toList();
	}

	/**
	 * Gets the data directory of the provided world.
	 * @param world the world.
	 * @return the data directory of the world.
	 */
	public static File dataDir(String world) {
        return new File(environmentDir(world), "data");
	}

	/**
	 * Gets the list of all map related data files.
	 * That is the file {@code data/idcounts.dat} and all the files with the pattern {@code data/map_*.dat}.
	 * @param world the world.
	 * @return the list of all map related data files
	 */
	public static List<File> mapFiles(String world) {
		Pattern mapFilePattern = Pattern.compile("map_\\d+.dat");
		return List.of(Objects.requireNonNull(dataDir(world).listFiles((dir, name) -> mapFilePattern.matcher(name).find() || "idcounts.dat".equals(name))));
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
