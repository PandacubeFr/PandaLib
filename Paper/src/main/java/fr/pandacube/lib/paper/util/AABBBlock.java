package fr.pandacube.lib.paper.util;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.BlockVector;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import fr.pandacube.lib.core.util.RandomUtil;

/**
 * Checkpoint represented as a 3D Axis and Block Aligned Bounding Box (sort of AABB).
 * Represent the littelest cuboid selection of blocks that contains the bounding box
 * passed to the constructor.
 */
public class AABBBlock implements Iterable<BlockVector> {
	
	public final Vector pos1, pos2;
	
	public AABBBlock(Vector p1, Vector p2) {
		this(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ(), p2.getBlockX(), p2.getBlockY(), p2.getBlockZ());
	}
	
	public AABBBlock(Location l1, Location l2) {
		this(l1.getBlockX(), l1.getBlockY(), l1.getBlockZ(), l2.getBlockX(), l2.getBlockY(), l2.getBlockZ());
	}
	
	public AABBBlock(int p1x, int p1y, int p1z, int p2x, int p2y, int p2z) {
		/*
		 * Prends les points extérieurs permettant de former un bouding box englobant
		 * celui représenté par v1 et v2, et étant aligné au quadrillage des blocs.
		 */
		pos1 = new Vector(Math.min(p1x, p2x),
				Math.min(p1y, p2y),
				Math.min(p1z, p2z));
		pos2 = new Vector(Math.max(p1x, p2x) + 1,
				Math.max(p1y, p2y) + 1,
				Math.max(p1z, p2z) + 1);
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
		return pos1.clone().add(pos2).multiply(0.5);
	}
	
	public BoundingBox asBukkitBoundingBox() {
		return new BoundingBox(pos1.getX(), pos1.getY(), pos1.getZ(),
				pos2.getX(), pos2.getY(), pos2.getZ());
	}
	
	public Vector getRandomPosition() {
		double x = RandomUtil.nextDoubleBetween(pos1.getX(), pos2.getX());
		double y = RandomUtil.nextDoubleBetween(pos1.getY(), pos2.getY());
		double z = RandomUtil.nextDoubleBetween(pos1.getZ(), pos2.getZ());
		return new Vector(x, y, z);
	}
	
	@Override
	public Iterator<BlockVector> iterator() {
		return new Iterator<BlockVector>() {
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
	
}