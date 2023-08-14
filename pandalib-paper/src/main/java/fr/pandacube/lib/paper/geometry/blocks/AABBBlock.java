package fr.pandacube.lib.paper.geometry.blocks;

import fr.pandacube.lib.util.RandomUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Iterator;

/**
 * Block Aligned Bounding Box (sort of AABB).
 * Represent the littlest cuboid selection of blocks that contains the bounding box
 * passed to the constructor.
 */
public class AABBBlock implements BlockSet, Cloneable {
	
	/* package */ final Vector pos1, pos2;
	private final Vector center;
	private final long volume;
	private BoundingBox bukkitBoundingBox;

	private AABBBlock(AABBBlock original, int shiftX, int shiftY, int shiftZ) {
		Vector shiftVec = new Vector(shiftX, shiftY, shiftZ);
		pos1 = original.pos1.clone().add(shiftVec);
		pos2 = original.pos2.clone().add(shiftVec);
		center = original.center.clone().add(shiftVec);
		volume = original.volume;
	}

	public AABBBlock(Vector p1, Vector p2) {
		this(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ(), p2.getBlockX(), p2.getBlockY(), p2.getBlockZ());
	}

	public AABBBlock(BoundingBox bb) {
		pos1 = bb.getMin();
		pos2 = bb.getMax();
		center = bb.getCenter();
		volume = (int) bb.getVolume();
		bukkitBoundingBox = bb;
	}
	
	public AABBBlock(Location l1, Location l2) {
		this(l1.getBlockX(), l1.getBlockY(), l1.getBlockZ(), l2.getBlockX(), l2.getBlockY(), l2.getBlockZ());
	}
	
	public AABBBlock(BlockVector l1, BlockVector l2) {
		this(l1.getBlockX(), l1.getBlockY(), l1.getBlockZ(), l2.getBlockX(), l2.getBlockY(), l2.getBlockZ());
	}
	
	public AABBBlock(int p1x, int p1y, int p1z, int p2x, int p2y, int p2z) {
		/*
		 * Prends les points extérieurs permettant de former un bounding box englobant
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

	@Override
	public AABBBlock getEnglobingAABB() {
		return this;
	}

	public Vector getMin() {
		return pos1.clone();
	}

	public Vector getMax() {
		return pos2.clone();
	}

	public BlockVector getMinBlock() {
		return pos1.toBlockVector();
	}

	public BlockVector getMaxBlock() {
		return pos2.clone().add(new Vector(-1, -1, -1)).toBlockVector();
	}

	public AABBBlock shift(int x, int y, int z) {
		return new AABBBlock(this, x, y, z);
	}

	@SuppressWarnings("MethodDoesntCallSuperMethod")
	@Override
	public AABBBlock clone() {
		return new AABBBlock(this, 0, 0, 0);
	}


	
	public boolean overlaps(BoundingBox bb) {
		return asBukkitBoundingBox().overlaps(bb);
	}

	public boolean isInside(Vector v) {
		return asBukkitBoundingBox().contains(v);
	}
	
	public Vector getCenter() {
		return center.clone();
	}
	
	public long getVolume() {
		return volume;
	}
	
	public BoundingBox asBukkitBoundingBox() {
		if (bukkitBoundingBox == null) {
			bukkitBoundingBox = new BoundingBox(pos1.getX(), pos1.getY(), pos1.getZ(),
					pos2.getX(), pos2.getY(), pos2.getZ());
		}
		return bukkitBoundingBox;
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




	static boolean overlap(AABBBlock aabb1, AABBBlock aabb2) {
		return aabb1.asBukkitBoundingBox().overlaps(aabb2.asBukkitBoundingBox());
	}

	static boolean overlap(AABBBlock aabb1, BlockSet bs) {
		if (!overlap(aabb1, bs.getEnglobingAABB()))
			return false;
		AABBBlock intersection = new AABBBlock(aabb1.asBukkitBoundingBox().intersection(bs.getEnglobingAABB().asBukkitBoundingBox()));
		for (BlockVector bv : intersection)
			if (bs.isInside(bv))
				return true;
		return false;
	}
	
}
