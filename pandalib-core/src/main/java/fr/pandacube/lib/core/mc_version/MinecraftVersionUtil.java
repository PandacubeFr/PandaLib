package fr.pandacube.lib.core.mc_version;

import fr.pandacube.lib.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Utility class to manipulate {@link String}s representing Minecraft versions.
 */
public class MinecraftVersionUtil {


    /**
     * Compare two Minecraft version strings. It uses the rules of semantic
     * versioning to compare the versions.
     * @param v1 the first version to compare.
     * @param v2 the second version to compare.
     * @return 0 if they are equal, &lt;0 if v1&lt;v2 and vice versa.
     */
    public static int compareVersions(String v1, String v2) {
        int[] v1Int = decomposedVersion(v1);
        int[] v2Int = decomposedVersion(v2);

        for (int i = 0; i < Math.min(v1Int.length, v2Int.length); i++) {
            int cmp = Integer.compare(v1Int[i], v2Int[i]);
            if (cmp != 0)
                return cmp;
        }

        return Integer.compare(v1Int.length, v2Int.length);
    }


    /**
     * Decompose a version string into a series of integers.
     * @param v a string representation of a version (e.g. 1.19.1).
     * @return an array of int representing the provided version (e.g. [1, 19, 1]).
     */
    public static int[] decomposedVersion(String v) {
        try {
            return Arrays.stream(v.split("\\.")).mapToInt(Integer::parseInt).toArray();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid version format: '" + v + "'.", e);
        }
    }

    /**
     * Tells if the two provided Minecraft versions are consecutive.
     * <p>
     * Two versions are consecutive if (considering {@code X.Y[.Z]}):
     * <ul>
     *     <li>They are part of the same main version (X.Y value, e.g. 1.19 or 26.2)</li>
     *     <li>v1 has no Z value, and v2 has Z = 1 (e.g. 1.19 and 1.19.1) OR
     *         both v1 and v2 has a Z value and those values are consecutive.
     *     </li>
     * </ul>
     * If at least 1 version string is less than 2 components or more than 3 components, it returns false.
     * @param v1 the first version.
     * @param v2 the second version.
     * @return true if the second version is consecutive to the first one.
     */
    public static boolean areConsecutive(String v1, String v2) {
        int[] v1Int = decomposedVersion(v1);
        int[] v2Int = decomposedVersion(v2);

        if (v1Int.length < 2 || v2Int.length < 2 || v1Int.length > 3 || v2Int.length > 3)
            return false;

        // compare the 2 first components
        if (v1Int[0] != v2Int[0] || v1Int[1] != v2Int[1]) {
            return false;
        }

        if (v1Int.length == 2) {      // v1 is x.y
            if (v2Int.length == 2) {  // v2 is x.y
                return true;
            }
            else {                    // v2 is x.y.?
                return v2Int[2] == 1; // v2 is x.y.1
            }
        }
        else {                        // v1 is x.y.?
            if (v2Int.length == 3) {  // v2 is x.y.?
                return v1Int[2] + 1 == v2Int[2]; // v1 is x.y.z and v2 is x.y.z+1
            }
        }
        return false;
    }




    /**
     * Generate a string representation of the provided list of version, with
     * merged consecutive versions and using
     * {@link StringUtil#joinGrammatically(CharSequence, CharSequence, List)}.
     *
     * @param versions the Minecraft versions list to use.
     * @param finalWordSeparator the word separator between the two last versions in the returned string, like "and",
     *                           "or" or any other word of any language. The spaces before and after are already
     *                           concatenated.
     * @return a string representation of the provided list of version.
     */
    public static String toString(List<String> versions, String finalWordSeparator) {
        if (versions.isEmpty())
            return "";
        // put them in order and remove duplicates
        versions = new ArrayList<>(toOrderedSet(versions));
        List<String> keptVersions = new ArrayList<>(versions.size());

        for (int i = 0, firstConsecutive = 0; i < versions.size(); i++) {
            if (i == versions.size() - 1 || !areConsecutive(versions.get(i), versions.get(i + 1))) {
                if (firstConsecutive == i) {
                    keptVersions.add(versions.get(i));
                    firstConsecutive++;
                }
                else {
                    // merge
                    if (i - firstConsecutive > 1)
                        keptVersions.add(versions.get(firstConsecutive) + " - " + versions.get(i));
                    else {
                        keptVersions.add(versions.get(firstConsecutive));
                        keptVersions.add(versions.get(i));
                    }
                    firstConsecutive = i + 1;
                }
            }
        }

        return StringUtil.joinGrammatically(", ", " " + finalWordSeparator + " ", keptVersions);
    }



    private static Set<String> toOrderedSet(List<String> versions) {
        Set<String> set = new TreeSet<>(MinecraftVersionUtil::compareVersions);
        set.addAll(versions);
        return set;
    }


    private MinecraftVersionUtil() {}

}
