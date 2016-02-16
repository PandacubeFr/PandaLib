package fr.pandacube.java.util;

public enum MinecraftVersion {
	v1_7_2_to_1_7_5(4),
	v1_7_6_to_1_7_10(5),
	v1_8(47);
	
	public final int versionNumber;
	
	private MinecraftVersion(int v) {
		versionNumber = v;
	}
	
	public static MinecraftVersion getVersion(int v) {
		for (MinecraftVersion mcV : MinecraftVersion.values())
			if (mcV.versionNumber == v)
				return mcV;
		
		return null;
	}
}
