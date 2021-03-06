package fr.pandacube.lib.paper.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import fr.pandacube.lib.util.IteratorIterator;
import fr.pandacube.lib.util.RandomUtil;

public class AABBBlockGroup implements Iterable<BlockVector> {
	
	public final List<AABBBlock> aabbBlocks;
	
	public AABBBlockGroup(Collection<AABBBlock> in) {
		aabbBlocks = List.copyOf(in);
	}
	
	public AABBBlockGroup(AABBBlock... in) {
		aabbBlocks = List.of(in);
	}
	
	
	public boolean isInside(Vector v) {
		for (AABBBlock b : aabbBlocks)
			if (b.isInside(v))
				return true;
		return false;
	}
	public boolean isInside(Location l) {
		return isInside(l.toVector());
	}
	public boolean isInside(Entity p) {
		return isInside(p.getLocation());
	}
	
	public Vector getRandomPosition() {
		double[] freq = aabbBlocks.stream().mapToDouble(AABBBlock::getVolume).toArray();
		int i = RandomUtil.randomIndexOfFrequencies(freq);
		return aabbBlocks.get(i).getRandomPosition();
	}
	
	public long getVolume() {
		long v = 0;
		for (AABBBlock b : aabbBlocks)
			v += b.getVolume();
		return v;
	}
	
	@Override
	public Iterator<BlockVector> iterator() {
		return IteratorIterator.ofCollectionOfIterator(aabbBlocks.stream().map(AABBBlock::iterator).toList());
	}
	
}
