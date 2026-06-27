package fr.pandacube.lib.paper.world;

import org.bukkit.NamespacedKey;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Utility class to ease the operations around the directory structure of a dimension
 * (like the overworld, nether, ender, and custom dimensions).
 * To handle operations around the level directory, use {@link LevelDir}.
 */
public class DimensionDir {

    /**
     * Get the dimension from the provided key in the provided level directory.
     * @param level the level directory.
     * @param key the dimension key.
     * @return the {@link DimensionDir}
     */
    public static DimensionDir fromLevel(final LevelDir level, final NamespacedKey key) {
        if (level.equals(ServerLevelDir.INSTANCE))
            return ServerDimensionDir.fromServerLevel(key);
        return new DimensionDir(level, key);
    }


    private final LevelDir level;
    private final NamespacedKey key;

    private final File directory;

    /**
     * Create the {@link DimensionDir} from the provided key in the provided level directory.
     * @param level the level directory.
     * @param dimensionKey the dimension key.
     */
    protected DimensionDir(LevelDir level, NamespacedKey dimensionKey) {
        this.level = level;
        this.key = dimensionKey;

        this.directory = new File(level.getDirectory(), "dimensions/" + key.namespace() + "/" + key.value());
    }

    /**
     * Get the directory of this dimension.
     * @return the directory of this dimension.
     */
    public File getDirectory() {
        return directory;
    }

    /**
     * Get the level in which this dimension is.
     * @return the level in which this dimension is
     */
    public LevelDir getLevel() {
        return level;
    }

    /**
     * Get the key of this dimension.
     * @return the key of this dimension.
     */
    public NamespacedKey getKey() {
        return key;
    }



    /**
     * Gets the list of all the regions of this dimension, based on the name of the region files.
     * @return a {@link List} of {@link RegionCoord}.
     */
    public List<RegionCoord> getRegions() {
        File regionDir = new File(getDirectory(), "region");

        String[] fileList = regionDir.list();
        if (fileList == null) {
            return List.of();
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
     * Gets all the directory containing region related data (entities, poi, region, DIM*) of this dimension.
     * @return a {@link List} of directory.
     */
    public List<File> getRegionDataFolders() {
        File envDir = getDirectory();
        return Stream.of("entities", "poi", "region")
                .map(f -> new File(envDir, f))
                .filter(File::exists)
                .toList();
    }



    /**
     * Gets the data directory of this dimension.
     * @return the data directory of this dimension.
     */
    public File getDataDirectory() {
        return new File(getDirectory(), "data");
    }


    /**
     * Tells if this level directory is a valid one (has a directory and contains a file named level.dat).
     * @return true if the level exists, false otherwise.
     */
    public boolean isValidDimension() {
        File d = getDataDirectory();
        return d.isDirectory() && new File(d, "minecraft/world_gen_settings.dat").isFile();
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof DimensionDir o
                && this.key.equals(o.key)
                && this.level.equals(o.level);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, key);
    }


    @Override
    public String toString() {
        return "DimensionDir(level=" + level + ";key=" + key + ")";
    }
}
