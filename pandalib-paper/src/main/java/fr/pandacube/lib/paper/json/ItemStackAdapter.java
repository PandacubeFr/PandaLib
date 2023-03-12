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
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.Map;

/* package */ class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    private static final TypeToken<ItemStack> ITEMSTACK_TYPE = TypeToken.get(ItemStack.class);
    public static final TypeAdapterFactory FACTORY = TreeTypeAdapter.newFactoryWithMatchRawType(ITEMSTACK_TYPE, new ItemStackAdapter());

    private static final TypeToken<Map<String, Object>> MAP_STR_OBJ_TYPE = new TypeToken<>() { };


    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return ItemStack.deserialize(context.deserialize(json, MAP_STR_OBJ_TYPE.getType()));
    }

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.serialize(), MAP_STR_OBJ_TYPE.getType());
    }
}
