package fr.pandacube.lib.paper.geometry.blocks;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.BlockVector;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public interface BlockSet extends Iterable<BlockVector> {

    Vector getRandomPosition();
    long getVolume();
    AABBBlock getEnglobingAABB();


    boolean overlaps(BoundingBox bb);
    default boolean overlaps(Entity e) {
        return overlaps(e.getBoundingBox());
    }
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


    boolean isInside(Vector v);
    default boolean isInside(Location l) {
        return isInside(l.toVector());
    }
    default boolean isInside(Block b) {
        return isInside(b.getLocation().add(.5, .5, .5));
    }
    default boolean isInside(Entity p) {
        return isInside(p.getLocation());
    }




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
