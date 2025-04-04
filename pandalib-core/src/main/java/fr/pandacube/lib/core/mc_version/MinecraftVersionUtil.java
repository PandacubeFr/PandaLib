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
     * @return 0 if they are equal, &lt;0 if v1&lt;v2 and vice-versa.
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
     * Two versions are consecutive if (considering {@code 1.X[.Y]}):
     * <ul>
     *     <li>They are part of the same main version (X value)</li>
     *     <li>v1 has no Y value, and v2 has Y = 1 (eg. 1.19 and 1.19.1) OR
     *         both v1 and v2 has a Y value and those values are consecutive.
     *     </li>
     * </ul>
     * @param v1 the first version.
     * @param v2 the second version.
     * @return thue if the second version is consecutive to the first one.
     */
    public static boolean areConsecutive(String v1, String v2) {
        int[] v1Int = decomposedVersion(v1);
        int[] v2Int = decomposedVersion(v2);

        if (v1Int.length == v2Int.length) {
            for (int i = 0; i < v1Int.length - 1; i++) {
                if (v1Int[i] != v2Int[i])
                    return false;
            }
            return v1Int[v1Int.length - 1] + 1 == v2Int[v2Int.length - 1];
        }
        else if (v1Int.length == v2Int.length - 1) {
            for (int i = 0; i < v1Int.length; i++) {
                if (v1Int[i] != v2Int[i])
                    return false;
            }
            return v2Int[v2Int.length - 1] == 1;
        }

        return false;
    }




    /**
     * Generate a string representation of the provided list of version, with
     * merged consecutive versions and using
     * {@link StringUtil#joinGrammatically(CharSequence, CharSequence, List)}.
     *
     * @param versions the minecraft versions list to use.
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
