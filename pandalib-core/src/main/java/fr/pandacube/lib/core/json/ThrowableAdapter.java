package fr.pandacube.lib.core.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.TreeTypeAdapter;
import fr.pandacube.lib.util.Log;
import fr.pandacube.lib.util.ThrowableUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Gson Adapter that handles serialization and deserialization of {@link Throwable} instances properly.
 */
public class ThrowableAdapter implements JsonSerializer<Throwable>, JsonDeserializer<Throwable> {

    /* package */ static final TypeAdapterFactory FACTORY = TreeTypeAdapter.newTypeHierarchyFactory(Throwable.class, new ThrowableAdapter());


    @Override
    public Throwable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject obj = json.getAsJsonObject();
        String message = obj.has("message") && !obj.get("message").isJsonNull()
                ? obj.get("message").getAsString() : null;
        Throwable cause = obj.has("cause") && !obj.get("cause").isJsonNull()
                ? context.deserialize(obj.get("cause"), Throwable.class) : null;

        // handle types
        Throwable t = null;
        if (obj.has("types") && obj.get("types").isJsonArray()) {
            t = instanciate(obj.getAsJsonArray("types"), message, cause);
        }
        if (t == null) {
            t = new Throwable(message, cause);
        }

        // handle suppressed
        JsonArray suppressed = obj.has("suppressed") && !obj.get("suppressed").isJsonNull()
                ? obj.get("suppressed").getAsJsonArray() : null;
        if (suppressed != null) {
            for (JsonElement jsonel : suppressed) {
                t.addSuppressed(context.deserialize(jsonel, Throwable.class));
            }
        }

        // handle stacktrace
        JsonArray stacktrace = obj.has("stacktrace") && !obj.get("stacktrace").isJsonNull()
                ? obj.get("stacktrace").getAsJsonArray() : null;
        if (stacktrace != null) {
            List<StackTraceElement> els = new ArrayList<>();
            for (JsonElement jsonel : stacktrace) {
                els.add(context.deserialize(jsonel, StackTraceElement.class));
            }
            t.setStackTrace(els.toArray(new StackTraceElement[0]));
        }

        return t;
    }

    @Override
    public JsonElement serialize(Throwable src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();

        // toString for easy json reading (not used for deserialization)
        json.addProperty("tostring", src.toString());

        // handle types
        JsonArray types = new JsonArray();
        Class<?> cl = src.getClass();
        while (cl != Throwable.class) {
            if (cl.getCanonicalName() != null)
                types.add(cl.getCanonicalName());
            cl = cl.getSuperclass();
        }
        json.add("types", types);

        // general data
        if (src.getMessage() != null)
            json.addProperty("message", src.getMessage());
        if (src.getCause() != null)
            json.add("cause", context.serialize(src.getCause()));

        // handle suppressed
        JsonArray suppressed = new JsonArray();
        for (Throwable supp : src.getSuppressed()) {
            suppressed.add(context.serialize(supp));
        }
        json.add("suppressed", suppressed);

        // handle stacktrace
        JsonArray stacktrace = new JsonArray();
        for (StackTraceElement stackTraceElement : src.getStackTrace()) {
            stacktrace.add(context.serialize(stackTraceElement));
        }
        json.add("stacktrace", stacktrace);

        return json;
    }








    private static final Map<Class<? extends Throwable>, ThrowableSubAdapter<?>> subAdapters = Collections.synchronizedMap(new HashMap<>());

    /**
     * Register a new adapter for a specific {@link Throwable} subclass.
     * @param clazz the type handled by the specified sub-adapter.
     * @param subAdapter the sub-adapter.
     * @param <T> the type.
     */
    public static <T extends Throwable> void registerSubAdapter(Class<T> clazz, ThrowableSubAdapter<T> subAdapter) {
        subAdapters.put(clazz, subAdapter);
    }

    private static <T extends Throwable> ThrowableSubAdapter<T> defaultSubAdapter(Class<T> clazz) {
        BiFunction<String, Throwable, T> constructor = null;

        // try (String, Throwable) constructor
        try {
            Constructor<T> constr = clazz.getConstructor(String.class, Throwable.class);
            if (constr.canAccess(null)) {
                constructor = (m, t) -> ThrowableUtil.wrapReflectEx(() -> constr.newInstance(m, t));
            }
        } catch (ReflectiveOperationException ignore) { }

        // try (String) constructor
        try {
            Constructor<T> constr = clazz.getConstructor(String.class);
            if (constr.canAccess(null)) {
                constructor = ThrowableSubAdapter.messageOnly((m) -> ThrowableUtil.wrapReflectEx(() -> constr.newInstance(m)));
            }
        } catch (ReflectiveOperationException ignore) { }

        if (constructor == null) {
            Log.warning("Provided Throwable class '" + clazz + "' does not have any of those constructors or are not accessible: (String, Throwable), (String).");
            return null;
        }

        return new ThrowableSubAdapter<>(constructor);
    }


    private Throwable instanciate(JsonArray types, String message, Throwable cause) {
        Throwable t = null;
        for (JsonElement clNameEl : types) {
            String clName = clNameEl.getAsString();
            try {
                @SuppressWarnings("unchecked")
                Class<? extends Throwable> cl = (Class<? extends Throwable>) Class.forName(clName);
                ThrowableSubAdapter<? extends Throwable> subAdapter = subAdapters.get(cl);
                if (subAdapter == null)
                    subAdapter = defaultSubAdapter(cl);

                if (subAdapter != null) {
                    t = subAdapter.constructor.apply(message, cause);
                    break;
                }
            } catch (ReflectiveOperationException ignore) { }
        }
        return t;
    }


    /**
     * Adapter for specific subclasses of {@link Throwable}.
     * @param <T> the type handled by this adapter.
     */
    public static class ThrowableSubAdapter<T extends Throwable> {
        private final BiFunction<String, Throwable, T> constructor;

        /**
         * Creates a new adapter for a {@link Throwable}.
         * @param constructor function that will construct a new throwable of the handled type, with prefilled message and cause if possible.
         */
        protected ThrowableSubAdapter(BiFunction<String, Throwable, T> constructor) {
            this.constructor = constructor;
        }

        /**
         * Utiliy method to use on {@link Throwable} class that only have a message (no cause) constructor.
         * @param constructorWithMessage function that will construct a new throwable, with prefilled message.
         * @return a function that will construct a throwable using the provided function, then will try to init the cause of the throwable.
         * @param <T> the type of the constructed {@link Throwable}.
         */
        public static <T extends Throwable> BiFunction<String, Throwable, T> messageOnly(Function<String, T> constructorWithMessage) {
            return (m, t) -> {
                T inst = constructorWithMessage.apply(m);
                try {
                    inst.initCause(t);
                } catch (Exception ignore) { }
                return inst;
            };
        }
    }


}
