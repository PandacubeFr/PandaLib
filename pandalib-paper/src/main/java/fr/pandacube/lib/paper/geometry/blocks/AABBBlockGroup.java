package fr.pandacube.lib.paper.geometry.blocks;

import fr.pandacube.lib.util.IteratorIterator;
import fr.pandacube.lib.util.RandomUtil;
import org.bukkit.util.BlockVector;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class AABBBlockGroup implements BlockSet {
	
	public final List<AABBBlock> subAABB;

	private final AABBBlock englobingAABB;

	public AABBBlockGroup(Collection<AABBBlock> in) {
		if (in.isEmpty())
			throw new IllegalArgumentException("Provided collection must not be empty.");
		subAABB = List.copyOf(in);
		englobingAABB = initEnglobingAABB();
	}
	
	public AABBBlockGroup(AABBBlock... in) {
		this(Arrays.asList(in));
	}

	private AABBBlock initEnglobingAABB() {
		Vector pos1 = subAABB.get(0).pos1.clone();
		Vector pos2 = subAABB.get(0).pos2.clone().add(new Vector(-1, -1, -1));
		for (int i = 1; i < subAABB.size(); i++) {
			AABBBlock aabb = subAABB.get(i);
			pos1.setX(Math.min(pos1.getBlockX(), aabb.pos1.getBlockX()));
			pos1.setY(Math.min(pos1.getBlockY(), aabb.pos1.getBlockY()));
			pos1.setZ(Math.min(pos1.getBlockZ(), aabb.pos1.getBlockZ()));
			pos2.setX(Math.max(pos2.getBlockX(), aabb.pos2.getBlockX() - 1));
			pos2.setY(Math.max(pos2.getBlockY(), aabb.pos2.getBlockY() - 1));
			pos2.setZ(Math.max(pos2.getBlockZ(), aabb.pos2.getBlockZ() - 1));
		}
		return new AABBBlock(pos1, pos2);
	}

	@Override
	public AABBBlock getEnglobingAABB() {
		return englobingAABB;
	}

	public boolean isInside(Vector v) {
		if (!englobingAABB.isInside(v))
			return false;
		for (AABBBlock b : subAABB)
			if (b.isInside(v))
				return true;
		return false;
	}
	
	public Vector getRandomPosition() {
		double[] freq = subAABB.stream().mapToDouble(AABBBlock::getVolume).toArray();
		int i = RandomUtil.randomIndexOfFrequencies(freq);
		return subAABB.get(i).getRandomPosition();
	}
	
	public long getVolume() {
		long v = 0;
		for (AABBBlock b : subAABB)
			v += b.getVolume();
		return v;
	}

	@Override
	public boolean overlaps(BoundingBox bb) {
		if (!englobingAABB.overlaps(bb))
			return false;
		for (AABBBlock b : subAABB)
			if (b.overlaps(bb))
				return true;
		return false;
	}

	@Override
	public Iterator<BlockVector> iterator() {
		return IteratorIterator.ofCollectionOfIterator(subAABB.stream().map(AABBBlock::iterator).toList());
	}

	@Override
	public String toString() {
		return "AABBBlockGroup{" +
				"subAABB=" + subAABB +
				", englobingAABB=" + englobingAABB +
				'}';
	}

	/* package */ static boolean overlap(AABBBlockGroup aabbGroup, AABBBlock aabb) {
		if (!aabbGroup.englobingAABB.overlaps(aabb))
			return false;
		for (AABBBlock b : aabbGroup.subAABB)
			if (b.overlaps(aabb))
				return true;
		return false;
	}

	/* package */ static boolean overlap(AABBBlockGroup aabbGroup1, AABBBlockGroup aabbGroup2) {
		if (!aabbGroup1.englobingAABB.overlaps(aabbGroup2.englobingAABB))
			return false;
		List<AABBBlock> group1SubList = new ArrayList<>();
		for (AABBBlock b : aabbGroup1.subAABB) {
			if (b.overlaps(aabbGroup2.englobingAABB))
				group1SubList.add(b);
		}
		if (group1SubList.isEmpty())
			return false;
		List<AABBBlock> group2SubList = new ArrayList<>();
		for (AABBBlock b : aabbGroup2.subAABB) {
			if (b.overlaps(aabbGroup1.englobingAABB))
				group2SubList.add(b);
		}
		if (group2SubList.isEmpty())
			return false;
		for (AABBBlock b1 : group1SubList)
			for (AABBBlock b2 : group2SubList)
				if (b1.overlaps(b2))
					return true;
		return false;
	}



	static boolean overlap(AABBBlockGroup aabbGroup, BlockSet bs) {
		if (!aabbGroup.englobingAABB.overlaps(bs.getEnglobingAABB()))
			return false;
		for (AABBBlock b : aabbGroup.subAABB) {
			if (b.overlaps(bs)) // already checks for englobingAABB before checking block per block
				return true;
		}
		return false;
	}
	
}
