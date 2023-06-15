package fr.pandacube.lib.core.mc_version;

import fr.pandacube.lib.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MinecraftVersionUtil {



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



    public static int[] decomposedVersion(String v) {
        try {
            return Arrays.stream(v.split("\\.")).mapToInt(Integer::parseInt).toArray();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid version format: '" + v + "'.", e);
        }
    }


    public static boolean areConsecutives(String v1, String v2) {
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




    public static String toString(List<String> versions, String finalWordSeparator) {
        if (versions.isEmpty())
            return "";
        // put them in order and remove duplicates
        versions = new ArrayList<>(toOrderedSet(versions));
        List<String> keptVersions = new ArrayList<>(versions.size());

        for (int i = 0, firstConsec = 0; i < versions.size(); i++) {
            if (i == versions.size() - 1 || !areConsecutives(versions.get(i), versions.get(i + 1))) {
                if (firstConsec == i) {
                    keptVersions.add(versions.get(i));
                    firstConsec++;
                }
                else {
                    // merge
                    if (i - firstConsec > 1)
                        keptVersions.add(versions.get(firstConsec) + "-" + versions.get(i));
                    else {
                        keptVersions.add(versions.get(firstConsec));
                        keptVersions.add(versions.get(i));
                    }
                    firstConsec = i + 1;
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

}
