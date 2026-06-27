package fr.pandacube.lib.paper.world;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Utility class to ease the operations around the directory structure of a Minecraft level.
 * To handle operations around the dimensions directory, use {@link DimensionDir}.
 */
public class LevelDir {

    /**
     * Get the LevelDir instance related to the currently loaded server level.
     * @return the unique {@link ServerLevelDir} instance.
     */
    public static ServerLevelDir ofServer() {
        return ServerLevelDir.INSTANCE;
    }

    /**
     * Get the level that is at the provided directory.
     * If the provided directory is the level currently loaded by the server, then {@link #ofServer()} is returned.
     * @param directory the directory of a Minecraft level.
     * @return the {@link LevelDir}.
     */
    public static LevelDir ofDirectory(File directory) {
        if (directory == null || directory.getAbsoluteFile().equals(ServerLevelDir.INSTANCE.getDirectory().getAbsoluteFile())) {
            return ofServer();
        }
        return new LevelDir(directory);
    }


    /**
     * Get the level as a sub directory of the current server's world container ({@link Bukkit#getWorldContainer()}).
     * @param name the directory name of a Minecraft level.
     * @return the {@link LevelDir}.
     */
    public static LevelDir named(String name) {
        return ofDirectory(name == null ? null : new File(Bukkit.getWorldContainer(), name));
    }




    private final File directory;

    /**
     * Create the level that is at the provided directory.
     * If the provided directory is the level currently loaded by the server, then {@link #ofServer()} is returned.
     * @param directory the directory of a Minecraft level.
     */
    protected LevelDir(final File directory) {
        this.directory = directory;
    }


    /**
     * Get the directory of this level.
     * @return the directory of this level.
     */
    public File getDirectory() {
        return directory;
    }


    /**
     * Get the dimension from the provided key in this level directory.
     * @param dim the dimension key.
     * @return a {@link DimensionDir}
     */
    public DimensionDir getDimension(NamespacedKey dim) {
        return DimensionDir.fromLevel(this, dim);
    }



    /**
     * Gets the data directory of the provided world.
     * @return the data directory of the world.
     */
    public File getDataDirectory() {
        return new File(getDirectory(), "data");
    }





    /**
     * Gets the list of all map related data files.
     * That is the file {@code data/minecraft/maps/last_id.dat} and all the files with the pattern {@code data/minecraft/maps/*.dat}.
     * @return the list of all map related data files
     */
    public List<File> getMapFiles() {
        File mapsDirectory = new File(getDataDirectory(), "minecraft/maps");
        Pattern mapFilePattern = Pattern.compile("\\d+.dat");
        return List.of(Objects.requireNonNull(mapsDirectory.listFiles((dir, name) -> mapFilePattern.matcher(name).find() || "last_id.dat".equals(name))));
    }



    /**
     * Tells if this level directory is a valid one (has a directory and contains a file named level.dat).
     * @return true if the level exists, false otherwise.
     */
    public boolean isValidLevel() {
        File d = getDirectory();
        return d.isDirectory() && new File(d, "level.dat").isFile();
    }




    @Override
    public boolean equals(Object obj) {
        return obj instanceof LevelDir o
                && this.directory.getAbsoluteFile().equals(o.directory.getAbsoluteFile());
    }

    @Override
    public int hashCode() {
        return directory.getAbsoluteFile().hashCode();
    }


    @Override
    public String toString() {
        return "LevelDir(" + directory.getAbsoluteFile() + ")";
    }
}
