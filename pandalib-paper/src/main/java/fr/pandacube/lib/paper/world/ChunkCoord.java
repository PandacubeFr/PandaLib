package fr.pandacube.lib.paper.world;

import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.concurrent.CompletableFuture;

/**
 * Represents a XZ chunk coordinates.
 * @param x the x coordinate.
 * @param z the z coordinate.
 */
public record ChunkCoord(int x, int z) {

    /**
     * Creates the {@link ChunkCoord} of a {@link Chunk}.
     * @param c the chunks from which to get its coordinates.
     */
    public ChunkCoord(Chunk c) {
        this(c.getX(), c.getZ());
    }

    @Override
    public String toString() {
        return "(" + x + ";" + z + ")";
    }

    /**
     * Gets the coordinates of the region file.
     * @return the {@link RegionCoord}.
     */
    public RegionCoord getRegionCoord() {
        return new RegionCoord(x >> 5, z >> 5);
    }

    /**
     * Get the {@link Chunk} at this coordinate in the provided World.
     * @param w the {@link World}.
     * @return a chunk, using {@link World#getChunkAt(int, int)}.
     */
    public Chunk getChunk(World w) {
        return w.getChunkAt(x, z);
    }

    /**
     * Get the {@link Chunk} at this coordinate in the provided World.
     * @param w the {@link World}.
     * @param generate Whether the chunk should be fully generated or not.
     * @return a chunk, using {@link World#getChunkAt(int, int, boolean)}.
     */
    public Chunk getChunk(World w, boolean generate) {
        return w.getChunkAt(x, z, generate);
    }

    /**
     * Get the {@link Chunk} at this coordinate in the provided World, asynchronously.
     * @param w the {@link World}.
     * @return a completable future of a chunk, using {@link World#getChunkAtAsync(int, int)}.
     */
    public CompletableFuture<Chunk> getChunkAsync(World w) {
        return w.getChunkAtAsync(x, z);
    }

    /**
     * Get the {@link Chunk} at this coordinate in the provided World, asynchronously.
     * @param w the {@link World}.
     * @param generate Whether the chunk should be fully generated or not.
     * @return a completable future of a chunk, using {@link World#getChunkAtAsync(int, int, boolean)}.
     */
    public CompletableFuture<Chunk> getChunkAsync(World w, boolean generate) {
        return w.getChunkAtAsync(x, z, generate);
    }
}
