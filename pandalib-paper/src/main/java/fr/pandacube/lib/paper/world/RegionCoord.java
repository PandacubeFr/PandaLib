package fr.pandacube.lib.paper.world;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record RegionCoord(int x, int z) {

	public int distToCenter() {
		return Math.max(Math.abs(x), Math.abs(z));
	}

	public String getFileName() {
		return "r." + x + "." + z + ".mca";
	}

	public ChunkCoord getMinChunk() {
		return new ChunkCoord(x << 5, z << 5);
	}

	public ChunkCoord getMaxChunk() {
		return new ChunkCoord(x << 5 | 31, z << 5 | 31);
	}



	private static final Pattern REGION_FILE_NAME_PATTERN = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");

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
