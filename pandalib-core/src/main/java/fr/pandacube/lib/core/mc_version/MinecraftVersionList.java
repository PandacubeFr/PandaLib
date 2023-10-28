package fr.pandacube.lib.core.mc_version;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.TreeTypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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


    /**
     * Gson Adapter that ensure the data in {@link MinecraftVersionList} is sorted correctly when deserializing.
     */
    public static class MinecraftVersionListAdapter implements JsonSerializer<MinecraftVersionList>, JsonDeserializer<MinecraftVersionList> {
        /**
         * Gson adapter factory for {@link MinecraftVersionList}.
         */
        public static final TypeAdapterFactory FACTORY = TreeTypeAdapter.newTypeHierarchyFactory(MinecraftVersionList.class, new MinecraftVersionListAdapter());

        private static final TypeToken<Map<String, Integer>> MAP_STR_INT_TYPE = new TypeToken<>() { };
        private static final TypeToken<Map<Integer, List<String>>> MAP_INT_LIST_STRING_TYPE = new TypeToken<>() { };
        @Override
        public MinecraftVersionList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!(json instanceof JsonObject jsonObj))
                throw new JsonParseException("Expected JsonObject, got " + json.getClass().getSimpleName() + ".");
            MinecraftVersionList mvList = new MinecraftVersionList();
            mvList.protocolOfVersion.putAll(context.deserialize(jsonObj.get("protocolOfVersion"), MAP_STR_INT_TYPE.getType()));
            mvList.versionsOfProtocol.putAll(context.deserialize(jsonObj.get("versionsOfProtocol"), MAP_INT_LIST_STRING_TYPE.getType()));
            for (List<String> versionLists : mvList.versionsOfProtocol.values()) {
                versionLists.sort(MinecraftVersionUtil::compareVersions);
            }
            return mvList;
        }

        @Override
        public JsonElement serialize(MinecraftVersionList src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.add("protocolOfVersion", context.serialize(src.protocolOfVersion));
            obj.add("versionsOfProtocol", context.serialize(src.versionsOfProtocol));
            return obj;
        }
    }
}
