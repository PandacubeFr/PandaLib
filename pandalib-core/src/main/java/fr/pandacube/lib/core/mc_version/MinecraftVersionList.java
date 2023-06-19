package fr.pandacube.lib.core.mc_version;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public record MinecraftVersionList(
        Map<String, Integer> protocolOfVersion,
        Map<Integer, List<String>> versionsOfProtocol
) {
    public MinecraftVersionList() {
        this(new TreeMap<>(MinecraftVersionUtil::compareVersions), new TreeMap<>());
    }

    public void add(String versionId, int protocolVersion) {
        protocolOfVersion.put(versionId, protocolVersion);
        List<String> versions = versionsOfProtocol.computeIfAbsent(protocolVersion, p -> new ArrayList<>());
        versions.add(versionId);
        versions.sort(MinecraftVersionUtil::compareVersions);
    }
}
