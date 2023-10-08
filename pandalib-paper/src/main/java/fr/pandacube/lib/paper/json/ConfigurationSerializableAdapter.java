package fr.pandacube.lib.paper.json;

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
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Gson adapter for ConfigurationSerializable, an interface implemented by several classes in the Bukkit API to ease
 * serialization to YAML.
 *
 * To not reinvent the wheel, this class uses the Bukkit’s Yaml API to convert the objects from/to json.
 */
/* package */ class ConfigurationSerializableAdapter implements JsonSerializer<ConfigurationSerializable>, JsonDeserializer<ConfigurationSerializable> {

    public static final TypeAdapterFactory FACTORY = TreeTypeAdapter.newTypeHierarchyFactory(ConfigurationSerializable.class, new ConfigurationSerializableAdapter());

    private static final TypeToken<Map<String, Object>> MAP_STR_OBJ_TYPE = new TypeToken<>() { };


    private boolean isItemStack(Map<String, Object> deserializedMap) {
        return deserializedMap.containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)
                && deserializedMap.get(ConfigurationSerialization.SERIALIZED_TYPE_KEY) instanceof String serializedType
                && ItemStack.class.equals(ConfigurationSerialization.getClassByAlias(serializedType));
    }

    @Override
    public ConfigurationSerializable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!(json instanceof JsonObject jsonObj) || !jsonObj.has(ConfigurationSerialization.SERIALIZED_TYPE_KEY))
            throw new JsonParseException("Unable to deserialize a ConfigurationSerializable from the provided json structure.");
        Map<String, Object> map = context.deserialize(jsonObj, MAP_STR_OBJ_TYPE.getType());
        if (isItemStack(map)) {
            ItemStackAdapter.fixDeserializationVersion(map);
        }
        String yaml = new Yaml().dump(Map.of("obj", map));
        YamlConfiguration cfg = new YamlConfiguration();
        try {
            cfg.loadFromString(yaml);
        } catch (InvalidConfigurationException e) {
            throw new JsonParseException("Unable t deserialize a ConfigurationSerializable from the provided json structure.", e);
        }
        return cfg.getSerializable("obj", ConfigurationSerializable.class);
    }

    @Override
    public JsonElement serialize(ConfigurationSerializable src, Type typeOfSrc, JsonSerializationContext context) {
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("obj", src);
        Map<String, Object> map = new Yaml().load(cfg.saveToString());
        return context.serialize(map.get("obj"), MAP_STR_OBJ_TYPE.getType());
    }


    /*public static void main(String[] args) {
        PaperJson.init();
        BlockVector bv = new BlockVector(12, 24, 48);
        String json = Json.gson.toJson(bv);
        System.out.println(json);
        BlockVector bv2 = Json.gson.fromJson(json, BlockVector.class);
        System.out.println(bv.equals(bv2));
    }*/
}
