package fr.pandacube.lib.paper.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.TreeTypeAdapter;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;

import java.lang.reflect.Type;

/* package */ class KeyAdapter implements JsonSerializer<Key>, JsonDeserializer<Key> {

    public static final TypeAdapterFactory FACTORY = TreeTypeAdapter.newTypeHierarchyFactory(Key.class, new KeyAdapter());

    @Override
    public Key deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        if (json == null || json.isJsonNull())
            return null;

        if (json instanceof JsonPrimitive jsonPrim) {
            String key = jsonPrim.getAsString();

            // NamespacedKey (Bukkit implementation of Adventure's Key interface)
            if (NamespacedKey.class.isAssignableFrom((Class<?>)typeOfT)) {
                Key ret = NamespacedKey.fromString(key);
                if (ret == null)
                    throw new JsonParseException(key + " is not a valid Key");
                return ret;
            }

            // defaults to KeyImpl (direct usage of Adventure's Key interface)
            else {
                try {
                    return Key.key(key);
                } catch (InvalidKeyException e) {
                    throw new JsonParseException(key + " is not a valid Key", e);
                }
            }
        }

        throw new JsonParseException("Unable to deserialize a NamespacedKey from the provided json structure.");
    }

    @Override
    public JsonElement serialize(Key src, Type typeOfSrc, JsonSerializationContext context) {
        return src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.asString());
    }


}
