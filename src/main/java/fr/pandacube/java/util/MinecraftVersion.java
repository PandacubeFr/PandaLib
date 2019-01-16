package fr.pandacube.java.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public enum MinecraftVersion {
	v1_7_2_to_1_7_5(4, "1.7.2-1.7.5"),
	v1_7_6_to_1_7_10(5, "1.7.6-1.7.10"),
	v1_8(47, "1.8.x"),
	v1_9(107, "1.9"),
	v1_9_1(108, "1.9.1"),
	v1_9_2(109, "1.9.2"),
	v1_9_3_to_1_9_4(110, "1.9.3-1.9.4"),
	v1_10(210, "1.10.x"),
	v1_11(315, "1.11"),
	v1_11_1_to_1_11_2(316, "1.11.1-1.11.2"),
	v1_12(335, "1.12"),
	v1_12_1(338, "1.12.1"),
	v1_12_2(340, "1.12.2"),
	v1_13(393, "1.13"),
	v1_13_1(401, "1.13.1"),
	v1_13_2(404, "1.13.2");

	public final int versionNumber;
	public final String versionDisplay;

	private MinecraftVersion(int v, String d) {
		versionNumber = v;
		versionDisplay = d;
	}

	@Override
	public String toString() {
		return versionDisplay;
	}

	public static MinecraftVersion getVersion(int v) {
		for (MinecraftVersion mcV : MinecraftVersion.values())
			if (mcV.versionNumber == v) return mcV;

		return null;
	}
	


	public static String displayOptimizedListOfVersions(List<MinecraftVersion> versions) {
		return String.join(", ", getVersionsDisplayList(versions));
	}
	
	
	public static final List<String> getVersionsDisplayList(List<MinecraftVersion> versions) {
		versions = new ArrayList<>(new HashSet<>(versions));
		versions.sort((a, b) -> Integer.compare(a.versionNumber, b.versionNumber));
		
		List<String> ret = new ArrayList<>();
		
		// grouping 1.7 versions
		if (versions.contains(v1_7_2_to_1_7_5) && versions.contains(v1_7_6_to_1_7_10)) {
			versions.remove(v1_7_2_to_1_7_5);
			versions.remove(v1_7_6_to_1_7_10);
			ret.add("1.7");
		}
		// grouping 1.8 versions
		if (versions.contains(v1_8)) {
			versions.remove(v1_8);
			ret.add("1.8");
		}
		// grouping 1.9 versions
		if (versions.contains(v1_9) && versions.contains(v1_9_1) && versions.contains(v1_9_2)
				&& versions.contains(v1_9_3_to_1_9_4)) {
			versions.remove(v1_9);
			versions.remove(v1_9_1);
			versions.remove(v1_9_2);
			versions.remove(v1_9_3_to_1_9_4);
			ret.add("1.9");
		}
		// grouping 1.10 versions
		if (versions.contains(v1_10)) {
			versions.remove(v1_10);
			ret.add("1.10");
		}
		// grouping 1.11 versions
		if (versions.contains(v1_11) && versions.contains(v1_11_1_to_1_11_2)) {
			versions.remove(v1_11);
			versions.remove(v1_11_1_to_1_11_2);
			ret.add("1.11");
		}
		// grouping 1.12 versions
		if (versions.contains(v1_12) && versions.contains(v1_12_1) && versions.contains(v1_12_2)) {
			versions.remove(v1_12);
			versions.remove(v1_12_1);
			versions.remove(v1_12_2);
			ret.add("1.12");
		}
		// grouping 1.13 versions
		if (versions.contains(v1_13) && versions.contains(v1_13_1) && versions.contains(v1_13_2)) {
			versions.remove(v1_13);
			versions.remove(v1_13_1);
			versions.remove(v1_13_2);
			ret.add("1.13");
		}
		
		for (MinecraftVersion v : versions)
			ret.add(v.versionDisplay);
		
		return ret;
		
	}

}
