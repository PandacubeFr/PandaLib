package fr.pandacube.lib.core.mc_version;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Record holding the data for {@link ProtocolVersion}, to facilitate serializing and deserializing.
 * @param protocolOfVersion mapping from a version string to the corresponding protocol version number.
 * @param versionsOfProtocol mapping from a protocol version number to a list of the supported MC versions.
 */
public record MinecraftVersionList(
        Map<String, Integer> protocolOfVersion,
        Map<Integer, List<String>> versionsOfProtocol
) {
    /**
     * Creates an empty {@link MinecraftVersionList}.
     */
    public MinecraftVersionList() {
        this(new TreeMap<>(MinecraftVersionUtil::compareVersions), new TreeMap<>());
    }

    /**
     * Adds a new pair of version string and protocol version number.
     * @param versionId the version string (e.g. "1.19.4").
     * @param protocolVersion the protocol version number.
     */
    public void add(String versionId, int protocolVersion) {
        protocolOfVersion.put(versionId, protocolVersion);
        List<String> versions = versionsOfProtocol.computeIfAbsent(protocolVersion, p -> new ArrayList<>());
        versions.add(versionId);
        versions.sort(MinecraftVersionUtil::compareVersions);
    }
}
