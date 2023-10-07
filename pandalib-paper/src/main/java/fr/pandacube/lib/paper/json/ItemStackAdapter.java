package fr.pandacube.lib.paper.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Type;
import java.util.Map;

/* package */ class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    public static final TypeAdapterFactory FACTORY = TreeTypeAdapter.newTypeHierarchyFactory(ItemStack.class, new ItemStackAdapter());

    private static final TypeToken<Map<String, Object>> MAP_STR_OBJ_TYPE = new TypeToken<>() { };

    /** Gson instance with no custom type adapter */
    private static final Gson vanillaGson = new GsonBuilder().setLenient().create();

    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!(json instanceof JsonObject jsonObj))
            throw new JsonParseException("Unable to deserialize a ConfigurationSerializable from the provided json structure.");
        if (jsonObj.has(ConfigurationSerialization.SERIALIZED_TYPE_KEY))
            return context.deserialize(jsonObj, ConfigurationSerializable.class);


        if (jsonObj.has("meta")
                && jsonObj.get("meta") instanceof JsonObject metaJson
                && !metaJson.has(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
            // item meta was serialized using GSON reflection serializer, instead of proper serialization using
            // ConfigurationSerializable interface. So we try to deserialize it the same way.

            Map<String, Object> map = context.deserialize(jsonObj, MAP_STR_OBJ_TYPE.getType());
            fixDeserializationVersion(map);
            map.remove("meta");
            ItemStack is = ItemStack.deserialize(map);

            Class<? extends ItemMeta> metaClass = is.getItemMeta().getClass();
            ItemMeta meta = vanillaGson.fromJson(jsonObj.get("meta"), metaClass);
            is.setItemMeta(meta);
            return is;
        }

        // deserialize using ConfigurationSerializableAdapter
        jsonObj.addProperty(ConfigurationSerialization.SERIALIZED_TYPE_KEY,
                ConfigurationSerialization.getAlias(ItemStack.class));
        return context.deserialize(jsonObj, ConfigurationSerializable.class);
    }

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.serialize(), MAP_STR_OBJ_TYPE.getType());
    }



    /* package */ static void fixDeserializationVersion(Map<String, Object> deserializedMap) {
        if (!deserializedMap.containsKey("v"))
            return;
        int itemStackVersion = ((Number)deserializedMap.get("v")).intValue();
        if (itemStackVersion >= 0) {
            @SuppressWarnings("deprecation")
            int currentDataVersion = Bukkit.getUnsafe().getDataVersion();
            if (itemStackVersion > currentDataVersion) {
                /* Here, the itemStack we are deserializing is from a newer MC version, so Bukkit will refuse it.
                 * We decide to ignore the provided version and consider that the received item stack is from current
                 * version. We let Bukkit handles the deserialization with the data it can interpret, throwing an error
                 * only if it can't.
                 */
                deserializedMap.put("v", currentDataVersion);
            }
        }
    }
}
