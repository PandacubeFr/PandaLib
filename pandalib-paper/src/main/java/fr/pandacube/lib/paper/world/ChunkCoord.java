package fr.pandacube.lib.paper.world;

import org.bukkit.Chunk;
import org.bukkit.World;

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
}
