package fr.pandacube.lib.core.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.TreeTypeAdapter;
import com.google.gson.stream.MalformedJsonException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Function;

/* package */ class ThrowableAdapter implements JsonSerializer<Throwable>, JsonDeserializer<Throwable> {

    public static final TypeAdapterFactory FACTORY = TreeTypeAdapter.newTypeHierarchyFactory(Throwable.class, new ThrowableAdapter());


    private static final Map<Class<? extends Throwable>, ThrowableSubAdapter<?>> subAdapters = Collections.synchronizedMap(new HashMap<>());

    public static <T extends Throwable> void registerSubAdapter(Class<T> clazz, ThrowableSubAdapter<T> subAdapter) {
        subAdapters.put(clazz, subAdapter);
    }

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
            for (JsonElement clNameEl : obj.getAsJsonArray("types")) {
                String clName = clNameEl.getAsString();
                try {
                    Class<?> cl = Class.forName(clName);
                    synchronized (subAdapters) {
                        if (subAdapters.containsKey(cl)) {
                            t = subAdapters.get(cl).constructor.apply(message, cause);
                            break;
                        }
                    }
                } catch (ReflectiveOperationException ignore) { }
            }
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





    public static class ThrowableSubAdapter<T extends Throwable> {
        public final BiFunction<String, Throwable, T> constructor;

        protected ThrowableSubAdapter(BiFunction<String, Throwable, T> constructor) {
            this.constructor = constructor;
        }

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


    static {
        // java.lang
        registerSubAdapter(Throwable.class, new ThrowableSubAdapter<>(Throwable::new));
        registerSubAdapter(Error.class, new ThrowableSubAdapter<>(Error::new));
        registerSubAdapter(OutOfMemoryError.class, new ThrowableSubAdapter<>(ThrowableSubAdapter.messageOnly(OutOfMemoryError::new)));
        registerSubAdapter(StackOverflowError.class, new ThrowableSubAdapter<>(ThrowableSubAdapter.messageOnly(StackOverflowError::new)));
        registerSubAdapter(Exception.class, new ThrowableSubAdapter<>(Exception::new));
        registerSubAdapter(RuntimeException.class, new ThrowableSubAdapter<>(RuntimeException::new));
        registerSubAdapter(NullPointerException.class, new ThrowableSubAdapter<>(ThrowableSubAdapter.messageOnly(NullPointerException::new)));
        registerSubAdapter(IndexOutOfBoundsException.class, new ThrowableSubAdapter<>(ThrowableSubAdapter.messageOnly(IndexOutOfBoundsException::new)));
        registerSubAdapter(IllegalArgumentException.class, new ThrowableSubAdapter<>(IllegalArgumentException::new));
        registerSubAdapter(IllegalStateException.class, new ThrowableSubAdapter<>(IllegalStateException::new));
        registerSubAdapter(SecurityException.class, new ThrowableSubAdapter<>(SecurityException::new));
        registerSubAdapter(ReflectiveOperationException.class, new ThrowableSubAdapter<>(ReflectiveOperationException::new));
        registerSubAdapter(UnsupportedOperationException.class, new ThrowableSubAdapter<>(UnsupportedOperationException::new));
        registerSubAdapter(InterruptedException.class, new ThrowableSubAdapter<>(ThrowableSubAdapter.messageOnly(InterruptedException::new)));

        // java.io
        registerSubAdapter(IOException.class, new ThrowableSubAdapter<>(IOException::new));

        // java.sql
        registerSubAdapter(SQLException.class, new ThrowableSubAdapter<>(SQLException::new));

        // java.util
        registerSubAdapter(NoSuchElementException.class, new ThrowableSubAdapter<>(NoSuchElementException::new));

        // java.util.concurrent
        registerSubAdapter(CancellationException.class, new ThrowableSubAdapter<>(ThrowableSubAdapter.messageOnly(CancellationException::new)));
        registerSubAdapter(ExecutionException.class, new ThrowableSubAdapter<>(ExecutionException::new));
        registerSubAdapter(CompletionException.class, new ThrowableSubAdapter<>(CompletionException::new));

        // gson
        registerSubAdapter(JsonIOException.class, new ThrowableSubAdapter<>(JsonIOException::new));
        registerSubAdapter(JsonParseException.class, new ThrowableSubAdapter<>(JsonParseException::new));
        registerSubAdapter(JsonSyntaxException.class, new ThrowableSubAdapter<>(JsonSyntaxException::new));
        registerSubAdapter(MalformedJsonException.class, new ThrowableSubAdapter<>(MalformedJsonException::new));
    }


}
