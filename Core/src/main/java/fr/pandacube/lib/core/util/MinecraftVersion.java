package fr.pandacube.lib.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;

public enum MinecraftVersion {
	v1_7_2_to_1_7_5(4, "1.7.2-1.7.5"),
	v1_7_6_to_1_7_10(5, "1.7.6-1.7.10"),
	
	v1_8(47, "1.8.x"),
	
	v1_9(107, "1.9"),
	v1_9_1(108, "1.9.1"),
	v1_9_2(109, "1.9.2"),
	v1_9_3_to_1_9_4(110, "1.9.3", "1.9.4"),
	
	v1_10(210, "1.10.x"),
	
	v1_11(315, "1.11"),
	v1_11_1_to_1_11_2(316, "1.11.1", "1.11.2"),
	
	v1_12(335, "1.12"),
	v1_12_1(338, "1.12.1"),
	v1_12_2(340, "1.12.2"),
	
	v1_13(393, "1.13"),
	v1_13_1(401, "1.13.1"),
	v1_13_2(404, "1.13.2"),
	
	v1_14(477, "1.14"),
	v1_14_1(480, "1.14.1"),
	v1_14_2(485, "1.14.2"),
	v1_14_3(490, "1.14.3"),
	v1_14_4(498, "1.14.4"),
	
	v1_15(573, "1.15"),
	v1_15_1(575, "1.15.1"),
	v1_15_2(578, "1.15.2"),
	
	v1_16(735, "1.16"),
	v1_16_1(736, "1.16.1"),
	v1_16_2(751, "1.16.2"),
	v1_16_3(753, "1.16.3"),
	v1_16_4_to_1_16_5(754, "1.16.4", "1.16.5"),

	v1_17(755, "1.17"),
	v1_17_1(756, "1.17.1");
	// IMPORTANT: don't forget to update the versionMergeDisplay value when adding a new version;
	
	private static Map<EnumSet<MinecraftVersion>, List<String>> versionMergeDisplay;
	
	static {
		versionMergeDisplay = new HashMap<>();
		
		versionMergeDisplay.put(EnumSet.of(v1_7_2_to_1_7_5, v1_7_6_to_1_7_10),
				ImmutableList.of("1.7.2-1.7.10"));
		
		versionMergeDisplay.put(EnumSet.of(v1_9, v1_9_1, v1_9_2, v1_9_3_to_1_9_4),
				ImmutableList.of("1.9.x"));
		versionMergeDisplay.put(EnumSet.of(v1_9, v1_9_1, v1_9_2),
				ImmutableList.of("1.9-1.9.2"));
		versionMergeDisplay.put(EnumSet.of(v1_9, v1_9_1),
				ImmutableList.of("1.9", "1.9.1"));
		versionMergeDisplay.put(EnumSet.of(v1_9_1, v1_9_2, v1_9_3_to_1_9_4),
				ImmutableList.of("1.9.1-1.9.4"));
		versionMergeDisplay.put(EnumSet.of(v1_9_1, v1_9_2),
				ImmutableList.of("1.9.1", "1.9.2"));
		versionMergeDisplay.put(EnumSet.of(v1_9_2, v1_9_3_to_1_9_4),
				ImmutableList.of("1.9.2-1.9.4"));

		versionMergeDisplay.put(EnumSet.of(v1_11, v1_11_1_to_1_11_2),
				ImmutableList.of("1.11.x"));

		versionMergeDisplay.put(EnumSet.of(v1_12, v1_12_1, v1_12_2),
				ImmutableList.of("1.12.x"));
		versionMergeDisplay.put(EnumSet.of(v1_12, v1_12_1),
				ImmutableList.of("1.12", "1.12.1"));
		versionMergeDisplay.put(EnumSet.of(v1_12_1, v1_12_2),
				ImmutableList.of("1.12.1", "1.12.2"));

		versionMergeDisplay.put(EnumSet.of(v1_13, v1_13_1, v1_13_2),
				ImmutableList.of("1.13.x"));
		versionMergeDisplay.put(EnumSet.of(v1_13, v1_13_1),
				ImmutableList.of("1.13", "1.13.1"));
		versionMergeDisplay.put(EnumSet.of(v1_13_1, v1_13_2),
				ImmutableList.of("1.13.1", "1.13.2"));

		versionMergeDisplay.put(EnumSet.of(v1_14, v1_14_1, v1_14_2, v1_14_3, v1_14_4),
				ImmutableList.of("1.14.x"));
		versionMergeDisplay.put(EnumSet.of(v1_14, v1_14_1, v1_14_2, v1_14_3),
				ImmutableList.of("1.14-1.14.3"));
		versionMergeDisplay.put(EnumSet.of(v1_14_1, v1_14_2, v1_14_3, v1_14_4),
				ImmutableList.of("1.14.1-1.14.4"));
		versionMergeDisplay.put(EnumSet.of(v1_14, v1_14_1, v1_14_2),
				ImmutableList.of("1.14-1.14.2"));
		versionMergeDisplay.put(EnumSet.of(v1_14_1, v1_14_2, v1_14_3),
				ImmutableList.of("1.14.1-1.14.3"));
		versionMergeDisplay.put(EnumSet.of(v1_14_2, v1_14_3, v1_14_4),
				ImmutableList.of("1.14.2-1.14.4"));
		versionMergeDisplay.put(EnumSet.of(v1_14, v1_14_1),
				ImmutableList.of("1.14", "1.14.1"));
		versionMergeDisplay.put(EnumSet.of(v1_14_1, v1_14_2),
				ImmutableList.of("1.14.1", "1.14.2"));
		versionMergeDisplay.put(EnumSet.of(v1_14_2, v1_14_3),
				ImmutableList.of("1.14.2", "1.14.3"));
		versionMergeDisplay.put(EnumSet.of(v1_14_3, v1_14_4),
				ImmutableList.of("1.14.3", "1.14.4"));
		
		versionMergeDisplay.put(EnumSet.of(v1_15, v1_15_1, v1_15_2),
				ImmutableList.of("1.15.x"));
		versionMergeDisplay.put(EnumSet.of(v1_15, v1_15_1),
				ImmutableList.of("1.15", "1.15.1"));
		versionMergeDisplay.put(EnumSet.of(v1_15_1, v1_15_2),
				ImmutableList.of("1.15.1", "1.15.2"));

		versionMergeDisplay.put(EnumSet.of(v1_16, v1_16_1, v1_16_2, v1_16_3, v1_16_4_to_1_16_5),
				ImmutableList.of("1.16.x"));
		versionMergeDisplay.put(EnumSet.of(v1_16, v1_16_1, v1_16_2, v1_16_3),
				ImmutableList.of("1.16-1.16.3"));
		versionMergeDisplay.put(EnumSet.of(v1_16_1, v1_16_2, v1_16_3, v1_16_4_to_1_16_5),
				ImmutableList.of("1.16.1-1.16.5"));
		versionMergeDisplay.put(EnumSet.of(v1_16, v1_16_1, v1_16_2),
				ImmutableList.of("1.16-1.16.2"));
		versionMergeDisplay.put(EnumSet.of(v1_16_1, v1_16_2, v1_16_3),
				ImmutableList.of("1.16.1-1.16.3"));
		versionMergeDisplay.put(EnumSet.of(v1_16_2, v1_16_3, v1_16_4_to_1_16_5),
				ImmutableList.of("1.16.2-1.16.5"));
		versionMergeDisplay.put(EnumSet.of(v1_16, v1_16_1),
				ImmutableList.of("1.16", "1.16.1"));
		versionMergeDisplay.put(EnumSet.of(v1_16_1, v1_16_2),
				ImmutableList.of("1.16.1", "1.16.2"));
		versionMergeDisplay.put(EnumSet.of(v1_16_2, v1_16_3),
				ImmutableList.of("1.16.2", "1.16.3"));
		versionMergeDisplay.put(EnumSet.of(v1_16_3, v1_16_4_to_1_16_5),
				ImmutableList.of("1.16.3-1.16.5"));
		
		versionMergeDisplay.put(EnumSet.of(v1_17, v1_17_1),
				ImmutableList.of("1.17.x"));
	}
	

	public final int id;
	public final List<String> versionDisplay;

	private MinecraftVersion(int v, String... d) {
		id = v;
		versionDisplay = Arrays.asList(d);
	}

	@Override
	public String toString() {
		return toStringAnd();
	}

	public String toStringAnd() {
		return StringUtil.joinGrammatically(", ", " et ", versionDisplay);
	}

	public String toStringOr() {
		return StringUtil.joinGrammatically(", ", " ou ", versionDisplay);
	}

	public static MinecraftVersion getVersion(int v) {
		for (MinecraftVersion mcV : values())
			if (mcV.id == v) return mcV;

		return null;
	}
	


	public static String displayOptimizedListOfVersionsAnd(List<MinecraftVersion> versions) {
		return StringUtil.joinGrammatically(", ", " et ", getVersionsDisplayList(versions));
	}

	public static String displayOptimizedListOfVersionsOr(List<MinecraftVersion> versions) {
		return StringUtil.joinGrammatically(", ", " ou ", getVersionsDisplayList(versions));
	}
	
	
	public static final List<String> getVersionsDisplayList(List<MinecraftVersion> vList) {
		if (vList == null)
			return new ArrayList<>();
		Set<MinecraftVersion> vSet = EnumSet.copyOf(vList);
		
		List<String> ret = new ArrayList<>();
		
		for (int i = 0; i < values().length; i++) {
			if (!vSet.contains(values()[i]))
				continue;
			
			EnumSet<MinecraftVersion> vSubSet = EnumSet.of(values()[i]);
			while (i + 1 < values().length && vSet.contains(values()[i + 1])) {
				i++;
				vSubSet.add(values()[i]);
				if (!versionMergeDisplay.containsKey(vSubSet)) {
					vSubSet.remove(values()[i]);
					i--;
					break;
				}
			}
			
			if (vSubSet.size() == 1) {
				ret.addAll(values()[i].versionDisplay);
			}
			else {
				ret.addAll(versionMergeDisplay.get(vSubSet));
			}
		}
		
		return ret;
		
	}

}
