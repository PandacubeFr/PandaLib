package fr.pandacube.lib.paper.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.TreeTypeAdapter;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.Map;

/* package */ class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    private static final TypeToken<ItemStack> ITEMSTACK_TYPE = TypeToken.get(ItemStack.class);
    public static final TypeAdapterFactory FACTORY = TreeTypeAdapter.newFactoryWithMatchRawType(ITEMSTACK_TYPE, new ItemStackAdapter());

    private static final TypeToken<Map<String, Object>> MAP_STR_OBJ_TYPE = new TypeToken<>() { };


    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<String, Object> deserializedMap = context.deserialize(json, MAP_STR_OBJ_TYPE.getType());
        int itemStackVersion = deserializedMap.containsKey("v") ? ((Number)deserializedMap.get("v")).intValue() : -1;
        if (itemStackVersion >= 0) {
            @SuppressWarnings("deprecation")
            int currentDataVersion = Bukkit.getUnsafe().getDataVersion();
            if (itemStackVersion > currentDataVersion) {
                /* The itemStack we are deserializing is from a newer MC version, so Bukkit will refuse it.
                 * We decide to ignore the provided version and consider that the received item stack is from current
                 * version. We let Bukkit handles the deserialization with the data it can interpret, throwing an error
                 * only if it can't.
                 */
                deserializedMap.put("v", currentDataVersion);
                return ItemStack.deserialize(deserializedMap);
            }
        }

        return ItemStack.deserialize(deserializedMap);
    }

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.serialize(), MAP_STR_OBJ_TYPE.getType());
    }
}
