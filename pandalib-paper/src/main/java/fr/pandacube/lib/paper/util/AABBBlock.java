package fr.pandacube.lib.paper.util;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.BlockVector;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import fr.pandacube.lib.util.RandomUtil;

/**
 * Checkpoint represented as a 3D Axis and Block Aligned Bounding Box (sort of AABB).
 * Represent the littelest cuboid selection of blocks that contains the bounding box
 * passed to the constructor.
 */
public class AABBBlock implements Iterable<BlockVector>, Cloneable {
	
	public final Vector pos1, pos2;

	private final Vector center;
	
	private final long volume;

	private AABBBlock(AABBBlock original, int shiftX, int shiftY, int shiftZ) {
		Vector shiftVect = new Vector(shiftX, shiftY, shiftZ);
		pos1 = original.pos1.clone().add(shiftVect);
		pos2 = original.pos2.clone().add(shiftVect);
		center = original.center.clone().add(shiftVect);
		volume = original.volume;
	}
	
	public AABBBlock(Vector p1, Vector p2) {
		this(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ(), p2.getBlockX(), p2.getBlockY(), p2.getBlockZ());
	}
	
	public AABBBlock(Location l1, Location l2) {
		this(l1.getBlockX(), l1.getBlockY(), l1.getBlockZ(), l2.getBlockX(), l2.getBlockY(), l2.getBlockZ());
	}
	
	public AABBBlock(BlockVector l1, BlockVector l2) {
		this(l1.getBlockX(), l1.getBlockY(), l1.getBlockZ(), l2.getBlockX(), l2.getBlockY(), l2.getBlockZ());
	}
	
	public AABBBlock(int p1x, int p1y, int p1z, int p2x, int p2y, int p2z) {
		/*
		 * Prends les points extérieurs permettant de former un bouding box englobant
		 * celui représenté par v1 et v2, et étant aligné au quadrillage des blocs.
		 */
		int p1x_ = Math.min(p1x, p2x);
		int p1y_ = Math.min(p1y, p2y);
		int p1z_ = Math.min(p1z, p2z);
		int p2x_ = Math.max(p1x, p2x) + 1;
		int p2y_ = Math.max(p1y, p2y) + 1;
		int p2z_ = Math.max(p1z, p2z) + 1;
		pos1 = new Vector(p1x_, p1y_, p1z_);
		pos2 = new Vector(p2x_, p2y_, p2z_);
		
		center = new Vector((p1x_ + p2x_) / 2d, (p1y_ + p2y_) / 2d, (p1z_ + p2z_) / 2d);
		
		volume = (long) Math.abs(p2x_ - p1x_) * Math.abs(p2x_ - p1x_) * Math.abs(p2x_ - p1x_);
	}

	public AABBBlock shift(int x, int y, int z) {
		return new AABBBlock(this, x, y, z);
	}

	@SuppressWarnings("MethodDoesntCallSuperMethod")
	@Override
	public AABBBlock clone() throws CloneNotSupportedException {
		return new AABBBlock(this, 0, 0, 0);
	}

	public boolean overlaps(Entity e) {
		return overlaps(e.getBoundingBox());
	}
	
	public boolean overlaps(BoundingBox bb) {
		return asBukkitBoundingBox().overlaps(bb);
	}
	
	public boolean isInside(Vector v) {
		return v.isInAABB(pos1, pos2);
	}
	public boolean isInside(Location l) {
		return isInside(l.toVector());
	}
	public boolean isInside(Entity p) {
		return isInside(p.getLocation());
	}
	
	public Vector getCenter() {
		return center.clone();
	}
	
	public long getVolume() {
		return volume;
	}
	
	public BoundingBox asBukkitBoundingBox() {
		return new BoundingBox(pos1.getX(), pos1.getY(), pos1.getZ(),
				pos2.getX(), pos2.getY(), pos2.getZ());
	}
	
	public Vector getRandomPosition() {
		double x = RandomUtil.rand.nextDouble(pos1.getX(), pos2.getX());
		double y = RandomUtil.rand.nextDouble(pos1.getY(), pos2.getY());
		double z = RandomUtil.rand.nextDouble(pos1.getZ(), pos2.getZ());
		return new Vector(x, y, z);
	}
	
	@Override
	public Iterator<BlockVector> iterator() {
		return new Iterator<>() {
			private int x = pos1.getBlockX(),
					y = pos1.getBlockY(),
					z = pos1.getBlockZ();

			@Override
			public boolean hasNext() {
				return x < pos2.getBlockX();
			}
			@Override
			public BlockVector next() {
				BlockVector bv = new BlockVector(x, y, z);

				z++;
				if (z >= pos2.getBlockZ()) {
					y++;
					z = pos1.getBlockZ();
					if (y >= pos2.getBlockY()) {
						x++;
						y = pos1.getBlockY();
					}
				}

				return bv;
			}
		};
	}
	
	
	public Iterable<Block> asBlockIterable(World w) {
		return () -> new Iterator<>() {
			final Iterator<BlockVector> nested = AABBBlock.this.iterator();
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
	
	
}
