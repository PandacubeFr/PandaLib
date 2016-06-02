package fr.pandacube.java.util;

public enum MinecraftVersion {
	v1_7_2_to_1_7_5(4, "1.7.2-1.7.5", false),
	v1_7_6_to_1_7_10(5, "1.7.6-1.7.10", false),
	v1_8(47, "1.8.x", true),
	v1_9(107, "1.9", false),
	v1_9_1(108, "1.9.1", false),
	v1_9_2(109, "1.9.2", false),
	v1_9_3_to_1_9_4(110, "1.9.3-1.9.4", true);
	
	public final int versionNumber;
	public final String versionDisplay;
	public final boolean available;
	
	private MinecraftVersion(int v, String d, boolean a) {
		versionNumber = v;
		versionDisplay = d;
		available = a;
	}
	
	
	public String toString() {
		return versionDisplay;
	}
	
	public static MinecraftVersion getVersion(int v) {
		for (MinecraftVersion mcV : MinecraftVersion.values())
			if (mcV.versionNumber == v)
				return mcV;
		
		return null;
	}
	
	
	public static String displayAvailableVersions() {
		boolean first = true;
		String concat = "";
		for (MinecraftVersion v : values()) {
			if (!v.available) continue;
			if (!first) concat += ", ";
			first = false;
			concat += v.versionDisplay;
		}
		
		return concat;
	}
}
