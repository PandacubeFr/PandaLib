package fr.pandacube.lib.paper.world;

import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.concurrent.CompletableFuture;

public record ChunkCoord(int x, int z) {

    public ChunkCoord(Chunk c) {
        this(c.getX(), c.getZ());
    }

    @Override
    public String toString() {
        return "(" + x + ";" + z + ")";
    }

    public RegionCoord getRegionCoord() {
        return new RegionCoord(x >> 5, z >> 5);
    }

    public Chunk getChunk(World w) {
        return w.getChunkAt(x, z);
    }

    public Chunk getChunk(World w, boolean generate) {
        return w.getChunkAt(x, z, generate);
    }

    public CompletableFuture<Chunk> getChunkAsync(World w) {
        return w.getChunkAtAsync(x, z);
    }

    public CompletableFuture<Chunk> getChunkAsync(World w, boolean generate) {
        return w.getChunkAtAsync(x, z, generate);
    }
}
