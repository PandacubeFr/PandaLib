package fr.pandacube.lib.core.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.TreeTypeAdapter;

import java.lang.reflect.Type;

/* package */ class StackTraceElementAdapter implements JsonSerializer<StackTraceElement>, JsonDeserializer<StackTraceElement> {

    public static final TypeAdapterFactory FACTORY = TreeTypeAdapter.newTypeHierarchyFactory(StackTraceElement.class, new StackTraceElementAdapter());


    @Override
    public StackTraceElement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        String classLoader = obj.has("classloader") && obj.get("classloader").isJsonPrimitive()
                ? obj.get("classloader").getAsString() : null;
        String module = obj.has("module") && obj.get("module").isJsonPrimitive()
                ? obj.get("module").getAsString() : null;
        String moduleVersion = obj.has("moduleversion") && obj.get("moduleversion").isJsonPrimitive()
                ? obj.get("moduleversion").getAsString() : null;
        String clazz = obj.has("class") && obj.get("class").isJsonPrimitive()
                ? obj.get("class").getAsString() : null;
        if (clazz == null) {
            throw new JsonParseException("Missing 'class' entry");
        }
        String method = obj.has("method") && obj.get("method").isJsonPrimitive()
                ? obj.get("method").getAsString() : null;
        if (method == null) {
            throw new JsonParseException("Missing 'method' entry");
        }
        String file = obj.has("file") && obj.get("file").isJsonPrimitive()
                ? obj.get("file").getAsString() : null;
        int line = obj.has("line") && obj.get("line").isJsonPrimitive()
                ? obj.get("line").getAsInt() : -1;

        return new StackTraceElement(classLoader, module, moduleVersion, clazz, method, file, line);
    }

    @Override
    public JsonElement serialize(StackTraceElement src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("class", src.getClassName());
        obj.addProperty("method", src.getMethodName());
        obj.addProperty("line", src.getLineNumber());
        if (src.getClassLoaderName() != null)
            obj.addProperty("classloader", src.getClassLoaderName());
        if (src.getModuleName() != null)
            obj.addProperty("module", src.getModuleName());
        if (src.getModuleVersion() != null)
            obj.addProperty("moduleversion", src.getModuleVersion());
        if (src.getFileName() != null)
            obj.addProperty("file", src.getFileName());
        return obj;
    }
}
