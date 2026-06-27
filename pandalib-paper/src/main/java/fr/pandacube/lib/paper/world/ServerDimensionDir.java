package fr.pandacube.lib.paper.world;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A dimension from the currently loaded server level.
 */
public class ServerDimensionDir extends DimensionDir {


    /**
     * An unmodifiable list containing the primary (vanilla) dimensions of this server instance, in the order they
     * ara loaded. This list can be accessed even if the corresponding worlds are not yet loaded, for instance in the
     * {@link Plugin#onLoad()} method.
     */
    public static final List<ServerDimensionDir> PRIMARY_DIMENSIONS;

    static {
        List<ServerDimensionDir> primaryDimensions = new ArrayList<>(3);

        primaryDimensions.add(fromServerLevel(NamespacedKey.minecraft("overworld")));
        if (Bukkit.getAllowNether()) {
            primaryDimensions.add(fromServerLevel(NamespacedKey.minecraft("the_nether")));
        }
        if (Bukkit.getAllowEnd()) {
            primaryDimensions.add(fromServerLevel(NamespacedKey.minecraft("the_end")));
        }

        PRIMARY_DIMENSIONS = Collections.unmodifiableList(primaryDimensions);
    }


    /**
     * Get a dimension of the provided key from the currently loaded server level.
     * @param key the dimension key.
     * @return a {@link ServerDimensionDir}.
     */
    public static ServerDimensionDir fromServerLevel(final NamespacedKey key) {
        return new ServerDimensionDir(key);
    }

    /**
     * Get a dimension from the provided Bukkit {@link World}.
     * @param world the Bukkit {@link World}.
     * @return a {@link ServerDimensionDir}.
     */
    public static ServerDimensionDir ofBukkitWorld(final World world) {
        return new ServerDimensionDir(world.getKey());
    }


    /* package */ ServerDimensionDir(NamespacedKey dim) {
        super(ServerLevelDir.INSTANCE, dim);
    }


    /**
     * Get the Bukkit {@link World} instance for this dimension, if loaded.
     * @return the Bukkit {@link World} instance for this dimension, or null if it's not loaded.
     */
    public World getBukkitWorld() {
        return Bukkit.getWorld(getKey());
    }

    /**
     * Tell if this dimension is loaded by the server.
     * @return true if this dimension is loaded, false otherwise.
     */
    public boolean isLoaded() {
        return getBukkitWorld() != null;
    }



}
