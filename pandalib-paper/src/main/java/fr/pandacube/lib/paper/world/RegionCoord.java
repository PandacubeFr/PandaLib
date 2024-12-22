package fr.pandacube.lib.paper.world;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a XZ region coordinates.
 * @param x the x coordinate.
 * @param z the z coordinate.
 */
public record RegionCoord(int x, int z) {

	/**
	 * Gets the <a href="https://en.wikipedia.org/wiki/Chebyshev_distance">Chebyshev distance</a> from this region to the center (0, 0) region.
	 * The unit is one region, i.e. the region (1, 0) has a distance of 1 from the center.
	 * @return the distance.
	 */
	public int distToCenter() {
		return Math.max(Math.abs(x), Math.abs(z));
	}

	/**
	 * Gets the file name that store this region.
	 * @return the region file name.
	 */
	public String getFileName() {
		return "r." + x + "." + z + ".mca";
	}

	/**
	 * Gets the chunk with the lowest coordinates of this region.
	 * @return the chunk with the lowest coordinates of this region.
	 */
	public ChunkCoord getMinChunk() {
		return new ChunkCoord(x << 5, z << 5);
	}

	/**
	 * Gets the chunk with the highest coordinates of this region.
	 * @return the chunk with the highest coordinates of this region.
	 */
	public ChunkCoord getMaxChunk() {
		return new ChunkCoord(x << 5 | 31, z << 5 | 31);
	}



	private static final Pattern REGION_FILE_NAME_PATTERN = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");

	/**
	 * Parse the coordinate from a region file name.
	 * @param name the name of the file to parse
	 * @return a new {@link RegionCoord}
	 * @throws IllegalArgumentException if the provided file name is not a valid MCA file name (including the extension).
	 */
	public static RegionCoord fromFileName(String name) {
		Matcher m = REGION_FILE_NAME_PATTERN.matcher(name);
		if (m.find()) {
			int x = Integer.parseInt(m.group(1));
			int z = Integer.parseInt(m.group(2));
			return new RegionCoord(x, z);
		}
		throw new IllegalArgumentException("Provided string is not a Minecraft region file name.");
	}

}
