package fr.pandacube.lib.paper.geometry.blocks;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.BlockVector;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Iterator;

/**
 * Represents a set of blocks in a world.
 */
public interface BlockSet extends Iterable<BlockVector> {

    /**
     * Gets a random coordinate that is inside this block set.
     * @return a random coordinate inside this block set.
     */
    Vector getRandomPosition();

    /**
     * Gets the volume, in blocks (or cubic meters), of this block set.
     * @return the volume of this block set.
     */
    long getVolume();

    /**
     * Gets the englobing bounding box if this block set.
     * @return the englobing bounding box if this block set.
     */
    AABBBlock getEnglobingAABB();


    /**
     * Tells if this block set overlaps the provided bounding box.
     * @param bb the provided bounding box
     * @return true if its overlaps, false otherwise.
     */
    boolean overlaps(BoundingBox bb);
    /**
     * Tells if this block set overlaps the bounding box of the provided entity.
     * @param e the provided entity.
     * @return true if its overlaps, false otherwise.
     */
    default boolean overlaps(Entity e) {
        return overlaps(e.getBoundingBox());
    }
    /**
     * Tells if this block set overlaps the provided one. that is there is at least one block in common.
     * @param bs the provided block set.
     * @return true if its overlaps, false otherwise.
     */
    default boolean overlaps(BlockSet bs) {
        if (this instanceof AABBBlock b1) {
            if (bs instanceof AABBBlock b2)
                return AABBBlock.overlap(b1, b2);
            if (bs instanceof AABBBlockGroup bg2)
                return AABBBlockGroup.overlap(bg2, b1);
            return AABBBlock.overlap(b1, bs);
        }
        if (this instanceof AABBBlockGroup bg1) {
            if (bs instanceof AABBBlock b2)
                return AABBBlockGroup.overlap(bg1, b2);
            if (bs instanceof AABBBlockGroup bg2)
                return AABBBlockGroup.overlap(bg1, bg2);
            return AABBBlockGroup.overlap(bg1, bs);
        }
        return overlap(this, bs);
    }


    /**
     * Tells if the provided vector is inside this bounding box.
     * @param v the vector.
     * @return true if its inside, false otherwise.
     */
    boolean isInside(Vector v);

    /**
     * Tells if the provided location is inside this bounding box.
     * The world of the location is ignored.
     * @param l the location.
     * @return true if its inside, false otherwise.
     */
    default boolean isInside(Location l) {
        return isInside(l.toVector());
    }

    /**
     * Tells if the provided block is inside this bounding box.
     * The world of the block is ignored.
     * @param b the block.
     * @return true if its inside, false otherwise.
     */
    default boolean isInside(Block b) {
        return isInside(b.getLocation().add(.5, .5, .5));
    }

    /**
     * Tells if the provided entity is inside this bounding box.
     * It calls {@link #isInside(Location)} using the returned value of {@link Entity#getLocation()}.
     * The world of the entity is ignored.
     * @param e the entity.
     * @return true if its inside, false otherwise.
     */
    default boolean isInside(Entity e) {
        return isInside(e.getLocation());
    }





    /**
     * Gets an iterator iterating through all the blocks of this block set.
     * @param w the world of the blocks.
     * @return a new iterator.
     */
    default Iterable<Block> asBlockIterable(World w) {
        return () -> new Iterator<>() {
            final Iterator<BlockVector> nested = BlockSet.this.iterator();
            @Override
            public boolean hasNext() {
                return nested.hasNext();
            }
            @Override
            public Block next() {
                BlockVector bv = nested.next();
                return w.getBlockAt(bv.getBlockX(), bv.getBlockY(), bv.getBlockZ());
            }
        };
    }



    /**
     * Tests the two block set overlap each other.
     * This method works on any implementation of this interface, but they should override the
     * {@link #overlaps(BlockSet)} method to provide a more optimized code.
     * @param bs1 the first block set.
     * @param bs2 the second block set.
     * @return true if the two block set overlap, false otherwise.
     */
    static boolean overlap(BlockSet bs1, BlockSet bs2) {
        if (!bs1.getEnglobingAABB().overlaps(bs2.getEnglobingAABB()))
            return false;
        AABBBlock intersection = new AABBBlock(bs1.getEnglobingAABB().asBukkitBoundingBox().intersection(bs2.getEnglobingAABB().asBukkitBoundingBox()));
        for (BlockVector bv : intersection)
            if (bs1.isInside(bv) && bs2.isInside(bv))
                return true;
        return false;
    }

}
