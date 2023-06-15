package fr.pandacube.lib.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Enumeration of all known, post Netty-rewrite (1.7.2+), stable Minecraft Java versions.
 * <p>
 * It provides various utility methods to nicely display a set of Minecraft version (for instance "1.13.x",
 * "1.16-1.16.3", "1.8.x and 1.9", "1.18.1 or 1.18.2")
 * <p>
 * Note that this enum uses one value to represent every Minecraft version using the same protocol version number.
 * @deprecated This class may not be updated. Use the class ProtocolVersion in pandalib-core module instead.
 */
@Deprecated
public enum MinecraftVersion {
	/** Minecraft versions 1.7.2 to 1.7.5, protocol version 4. */
	v1_7_2_to_1_7_5(4, "1.7.2", "1.7.3", "1.7.4", "1.7.5"),
	/** Minecraft versions 1.7.6 to 1.7.10, protocol version 5. */
	v1_7_6_to_1_7_10(5, "1.7.6", "1.7.7", "1.7.8", "1.7.9", "1.7.10"),

	/** Minecraft versions 1.8.x, protocol version 47. */
	v1_8(47, "1.8", "1.8.1", "1.8.2", "1.8.3", "1.8.4", "1.8.5", "1.8.6", "1.8.7", "1.8.8", "1.8.9"),

	/** Minecraft version 1.9, protocol version 107. */
	v1_9(107, "1.9"),
	/** Minecraft version 1.9.1, protocol version 108. */
	v1_9_1(108, "1.9.1"),
	/** Minecraft version 1.9.2, protocol version 109. */
	v1_9_2(109, "1.9.2"),
	/** Minecraft versions 1.9.3 and 1.9.4, protocol version 110. */
	v1_9_3_to_1_9_4(110, "1.9.3", "1.9.4"),

	/** Minecraft versions 1.10.x, protocol version 210. */
	v1_10(210, "1.10", "1.10.1", "1.10.2"),

	/** Minecraft version 1.11, protocol version 315. */
	v1_11(315, "1.11"),
	/** Minecraft versions 1.11.1 and 1.11.2, protocol version 316. */
	v1_11_1_to_1_11_2(316, "1.11.1", "1.11.2"),

	/** Minecraft version 1.12, protocol version 335. */
	v1_12(335, "1.12"),
	/** Minecraft version 1.12.1, protocol version 338. */
	v1_12_1(338, "1.12.1"),
	/** Minecraft version 1.12.2, protocol version 340. */
	v1_12_2(340, "1.12.2"),

	/** Minecraft version 1.13, protocol version 393. */
	v1_13(393, "1.13"),
	/** Minecraft version 1.13.1, protocol version 401. */
	v1_13_1(401, "1.13.1"),
	/** Minecraft version 1.13.2, protocol version 404. */
	v1_13_2(404, "1.13.2"),

	/** Minecraft version 1.14, protocol version 477. */
	v1_14(477, "1.14"),
	/** Minecraft version 1.14.1, protocol version 480. */
	v1_14_1(480, "1.14.1"),
	/** Minecraft version 1.14.2, protocol version 485. */
	v1_14_2(485, "1.14.2"),
	/** Minecraft version 1.14.3, protocol version 490. */
	v1_14_3(490, "1.14.3"),
	/** Minecraft version 1.14.4, protocol version 498. */
	v1_14_4(498, "1.14.4"),

	/** Minecraft version 1.15, protocol version 573. */
	v1_15(573, "1.15"),
	/** Minecraft version 1.15.1, protocol version 575. */
	v1_15_1(575, "1.15.1"),
	/** Minecraft version 1.15.2, protocol version 578. */
	v1_15_2(578, "1.15.2"),

	/** Minecraft version 1.16, protocol version 735. */
	v1_16(735, "1.16"),
	/** Minecraft version 1.16.1, protocol version 736. */
	v1_16_1(736, "1.16.1"),
	/** Minecraft version 1.16.2, protocol version 751. */
	v1_16_2(751, "1.16.2"),
	/** Minecraft version 1.16.3, protocol version 753. */
	v1_16_3(753, "1.16.3"),
	/** Minecraft versions 1.16.4 and 1.16.5, protocol version 754. */
	v1_16_4_to_1_16_5(754, "1.16.4", "1.16.5"),

	/** Minecraft version 1.17, protocol version 755. */
	v1_17(755, "1.17"),
	/** Minecraft version 1.17.1, protocol version 756. */
	v1_17_1(756, "1.17.1"),

	/** Minecraft versions 1.18 and 1.18.1, protocol version 757. */
	v1_18_to_1_18_1(757, "1.18", "1.18.1"),
	/** Minecraft version 1.18.2, protocol version 758. */
	v1_18_2(758, "1.18.2"),

	/** Minecraft version 1.19, protocol version 759. */
	v1_19(759, "1.19"),
	/** Minecraft versions 1.19.1 and 1.19.2, protocol version 760. */
	v1_19_1_to_1_19_2(760, "1.19.1", "1.19.2"),
	/** Minecraft versions 1.19.3, protocol version 761. */
	v1_19_3(761, "1.19.3"),
	/** Minecraft versions 1.19.4, protocol version 762. */
	v1_19_4(762, "1.19.4"),

	/** Minecraft version 1.20 and 1.20.1, protocol version 763. */
	v1_20(763, "1.20", "1.20.1");

	// IMPORTANT: don't forget to update the versionMergeDisplay value when adding a new version;
	
	private static final Map<EnumSet<MinecraftVersion>, List<String>> versionMergeDisplay;
	
	static {
		versionMergeDisplay = new HashMap<>();
		
		versionMergeDisplay.put(EnumSet.of(v1_7_2_to_1_7_5, v1_7_6_to_1_7_10),
				List.of("1.7.2-1.7.10"));
		
		versionMergeDisplay.put(EnumSet.of(v1_9, v1_9_1, v1_9_2, v1_9_3_to_1_9_4),
				List.of("1.9.x"));
		versionMergeDisplay.put(EnumSet.of(v1_9, v1_9_1, v1_9_2),
				List.of("1.9-1.9.2"));
		versionMergeDisplay.put(EnumSet.of(v1_9, v1_9_1),
				List.of("1.9", "1.9.1"));
		versionMergeDisplay.put(EnumSet.of(v1_9_1, v1_9_2, v1_9_3_to_1_9_4),
				List.of("1.9.1-1.9.4"));
		versionMergeDisplay.put(EnumSet.of(v1_9_1, v1_9_2),
				List.of("1.9.1", "1.9.2"));
		versionMergeDisplay.put(EnumSet.of(v1_9_2, v1_9_3_to_1_9_4),
				List.of("1.9.2-1.9.4"));

		versionMergeDisplay.put(EnumSet.of(v1_11, v1_11_1_to_1_11_2),
				List.of("1.11.x"));

		versionMergeDisplay.put(EnumSet.of(v1_12, v1_12_1, v1_12_2),
				List.of("1.12.x"));
		versionMergeDisplay.put(EnumSet.of(v1_12, v1_12_1),
				List.of("1.12", "1.12.1"));
		versionMergeDisplay.put(EnumSet.of(v1_12_1, v1_12_2),
				List.of("1.12.1", "1.12.2"));

		versionMergeDisplay.put(EnumSet.of(v1_13, v1_13_1, v1_13_2),
				List.of("1.13.x"));
		versionMergeDisplay.put(EnumSet.of(v1_13, v1_13_1),
				List.of("1.13", "1.13.1"));
		versionMergeDisplay.put(EnumSet.of(v1_13_1, v1_13_2),
				List.of("1.13.1", "1.13.2"));

		versionMergeDisplay.put(EnumSet.of(v1_14, v1_14_1, v1_14_2, v1_14_3, v1_14_4),
				List.of("1.14.x"));
		versionMergeDisplay.put(EnumSet.of(v1_14, v1_14_1, v1_14_2, v1_14_3),
				List.of("1.14-1.14.3"));
		versionMergeDisplay.put(EnumSet.of(v1_14_1, v1_14_2, v1_14_3, v1_14_4),
				List.of("1.14.1-1.14.4"));
		versionMergeDisplay.put(EnumSet.of(v1_14, v1_14_1, v1_14_2),
				List.of("1.14-1.14.2"));
		versionMergeDisplay.put(EnumSet.of(v1_14_1, v1_14_2, v1_14_3),
				List.of("1.14.1-1.14.3"));
		versionMergeDisplay.put(EnumSet.of(v1_14_2, v1_14_3, v1_14_4),
				List.of("1.14.2-1.14.4"));
		versionMergeDisplay.put(EnumSet.of(v1_14, v1_14_1),
				List.of("1.14", "1.14.1"));
		versionMergeDisplay.put(EnumSet.of(v1_14_1, v1_14_2),
				List.of("1.14.1", "1.14.2"));
		versionMergeDisplay.put(EnumSet.of(v1_14_2, v1_14_3),
				List.of("1.14.2", "1.14.3"));
		versionMergeDisplay.put(EnumSet.of(v1_14_3, v1_14_4),
				List.of("1.14.3", "1.14.4"));
		
		versionMergeDisplay.put(EnumSet.of(v1_15, v1_15_1, v1_15_2),
				List.of("1.15.x"));
		versionMergeDisplay.put(EnumSet.of(v1_15, v1_15_1),
				List.of("1.15", "1.15.1"));
		versionMergeDisplay.put(EnumSet.of(v1_15_1, v1_15_2),
				List.of("1.15.1", "1.15.2"));

		versionMergeDisplay.put(EnumSet.of(v1_16, v1_16_1, v1_16_2, v1_16_3, v1_16_4_to_1_16_5),
				List.of("1.16.x"));
		versionMergeDisplay.put(EnumSet.of(v1_16, v1_16_1, v1_16_2, v1_16_3),
				List.of("1.16-1.16.3"));
		versionMergeDisplay.put(EnumSet.of(v1_16_1, v1_16_2, v1_16_3, v1_16_4_to_1_16_5),
				List.of("1.16.1-1.16.5"));
		versionMergeDisplay.put(EnumSet.of(v1_16, v1_16_1, v1_16_2),
				List.of("1.16-1.16.2"));
		versionMergeDisplay.put(EnumSet.of(v1_16_1, v1_16_2, v1_16_3),
				List.of("1.16.1-1.16.3"));
		versionMergeDisplay.put(EnumSet.of(v1_16_2, v1_16_3, v1_16_4_to_1_16_5),
				List.of("1.16.2-1.16.5"));
		versionMergeDisplay.put(EnumSet.of(v1_16, v1_16_1),
				List.of("1.16", "1.16.1"));
		versionMergeDisplay.put(EnumSet.of(v1_16_1, v1_16_2),
				List.of("1.16.1", "1.16.2"));
		versionMergeDisplay.put(EnumSet.of(v1_16_2, v1_16_3),
				List.of("1.16.2", "1.16.3"));
		versionMergeDisplay.put(EnumSet.of(v1_16_3, v1_16_4_to_1_16_5),
				List.of("1.16.3-1.16.5"));
		
		versionMergeDisplay.put(EnumSet.of(v1_17, v1_17_1),
				List.of("1.17.x"));
		
		versionMergeDisplay.put(EnumSet.of(v1_18_to_1_18_1, v1_18_2),
				List.of("1.18.x"));

		versionMergeDisplay.put(EnumSet.of(v1_19, v1_19_1_to_1_19_2, v1_19_3, v1_19_4),
				List.of("1.19.x"));
		versionMergeDisplay.put(EnumSet.of(v1_19, v1_19_1_to_1_19_2, v1_19_3),
				List.of("1.19-1.19.3"));
		versionMergeDisplay.put(EnumSet.of(v1_19_1_to_1_19_2, v1_19_3, v1_19_4),
				List.of("1.19.1-1.19.4"));
		versionMergeDisplay.put(EnumSet.of(v1_19, v1_19_1_to_1_19_2),
				List.of("1.19-1.19.2"));
		versionMergeDisplay.put(EnumSet.of(v1_19_1_to_1_19_2, v1_19_3),
				List.of("1.19.1-1.19.3"));
		versionMergeDisplay.put(EnumSet.of(v1_19_3, v1_19_4),
				List.of("1.19.3-1.19.4"));
	}


	/**
	 * The protocol version number of this Minecraft version.
	 */
	public final int protocolVersionNumber;
	/**
	 * All Minecraft version supported by this protocol version number.
	 */
	public final List<String> versionsDisplay;

	MinecraftVersion(int protocolVersionNumber, String... versionsDisplay) {
		this.protocolVersionNumber = protocolVersionNumber;
		this.versionsDisplay = Arrays.asList(versionsDisplay);
	}

	@Override
	public String toString() {
		return name() + "{protocol=" + protocolVersionNumber + ", toString(\"and\")=" + toString("and") + "}";
	}

	/**
	 * Returns a string representation of all the Minecraft version of this enum value, using
	 * {@link StringUtil#joinGrammatically(CharSequence, CharSequence, List)}.
	 *
	 * @param finalWordSeparator the word separator between the two last versions in the returned string, like "and",
	 *                           "or" or any other word of any language. The spaces before and after are already
	 *                           concatenated.
	 * @return a string representation of this {@link MinecraftVersion}.
	 */
	public String toString(String finalWordSeparator) {
		return StringUtil.joinGrammatically(", ", " " + finalWordSeparator + " ", versionsDisplay);
	}

	/**
	 * Returns a string representation of all the Minecraft version of this enum value, using
	 * {@link StringUtil#joinGrammatically(CharSequence, CharSequence, List)} with the gramatical word "et"
	 * ("and" in french).
	 *
	 * @return a string representation of this {@link MinecraftVersion}.
	 * @deprecated it uses the hardcoded french word "et" as the final word separator.
	 *             Use {@link #displayOptimizedListOfVersions(List, String)} with "et" as the last parameter instead.
	 */
	@Deprecated
	public String toStringAnd() {
		return toString("et");
	}

	/**
	 * Returns a string representation of all the Minecraft version of this enum value, using
	 * {@link StringUtil#joinGrammatically(CharSequence, CharSequence, List)} with the gramatical word "ou"
	 * ("or" in french).
	 *
	 * @return a string representation of this {@link MinecraftVersion}.
	 * @deprecated it uses the hardcoded french word "ou" as the final word separator.
	 *             Use {@link #displayOptimizedListOfVersions(List, String)} with "ou" as the last parameter instead.
	 */
	@Deprecated
	public String toStringOr() {
		return toString("ou");
	}








	/**
	 * Gets the {@link MinecraftVersion} instance associated with the provided protocol version number.
	 *
	 * @param protocolVersionNumber the protocol version number
	 * @return the {@link MinecraftVersion} instance associated with the provided protocol version number, or null if
	 *         there is none.
	 */
	public static MinecraftVersion getVersion(int protocolVersionNumber) {
		for (MinecraftVersion mcV : values())
			if (mcV.protocolVersionNumber == protocolVersionNumber) return mcV;
		return null;
	}


	/**
	 * Generate a string representation of the provided list of version, using
	 * {@link StringUtil#joinGrammatically(CharSequence, CharSequence, List)}.
	 *
	 * @param versions the minecraft versions to list
	 * @param finalWordSeparator the word separator between the two last versions in the returned string, like "and",
	 *                           "or" or any other word of any language. The spaces before and after are already
	 *                           concatenated.
	 * @return a string representation of the provided list of version.
	 */
	public static String displayOptimizedListOfVersions(List<MinecraftVersion> versions, String finalWordSeparator) {
		return StringUtil.joinGrammatically(", ", " " + finalWordSeparator + " ", getVersionsDisplayList(versions));
	}

	/**
	 * Generate a string representation of the provided list of version, using
	 * {@link StringUtil#joinGrammatically(CharSequence, CharSequence, List)} with the gramatical word "et"
	 * ("and" in french).
	 *
	 * @param versions the minecraft versions to list
	 * @return a string representation of the provided list of version.
	 * @deprecated it uses the hardcoded french word "et" as the final word separator.
	 *             Use {@link #displayOptimizedListOfVersions(List, String)} with "et" as the last parameter instead.
	 */
	@Deprecated
	public static String displayOptimizedListOfVersionsAnd(List<MinecraftVersion> versions) {
		return displayOptimizedListOfVersions(versions, "et");
	}

	/**
	 * Generate a string representation of the provided list of version, using
	 * {@link StringUtil#joinGrammatically(CharSequence, CharSequence, List)} with the gramatical word "ou"
	 * ("or" in french).
	 *
	 * @param versions the minecraft versions to list
	 * @return a string representation of the provided list of version.
	 * @deprecated it uses the hardcoded french word "ou" as the final word separator.
	 *             Use {@link #displayOptimizedListOfVersions(List, String)} with "ou" as the last parameter instead.
	 */
	@Deprecated
	public static String displayOptimizedListOfVersionsOr(List<MinecraftVersion> versions) {
		return displayOptimizedListOfVersions(versions, "ou");
	}

	/**
	 * Returns an optimized list of string representation of Minecraft version, that represent the provided list of
	 * Minecraft version.
	 * <p>
	 * This methods try to merge successive Minecraft version into a single string: for instance, all versions from 1.18
	 * to 1.18.2 are represented by the string "1.18.x"; all version from 1.14.1 to 1.14.4 are represented by the string
	 * "1.14.1-1.14.4".
	 * <p>
	 * All possible merges of {@link MinecraftVersion} are listed in the static initializer of this enum.
	 *
	 * @param vList the {@link List} of {@link MinecraftVersion}
	 * @return an optimized list of string representation of Minecraft version.
	 */
	public static List<String> getVersionsDisplayList(List<MinecraftVersion> vList) {
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
				ret.addAll(values()[i].versionsDisplay);
			}
			else {
				ret.addAll(versionMergeDisplay.get(vSubSet));
			}
		}
		
		return ret;
		
	}

}
