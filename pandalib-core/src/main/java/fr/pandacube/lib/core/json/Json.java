package fr.pandacube.lib.core.json;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * Provides pre-instanciated {@link Gson} instances, all with support for Java records and additionnal
 * {@link TypeAdapterFactory} provided with {@link #registerTypeAdapterFactory(TypeAdapterFactory)}.
 */
public class Json {

	/**
	 * {@link Gson} instance with {@link GsonBuilder#setLenient()} and support for Java records and additionnal
	 * {@link TypeAdapterFactory} provided with {@link #registerTypeAdapterFactory(TypeAdapterFactory)}.
	 */
	public static final Gson gson = build(Function.identity());

	/**
	 * {@link Gson} instance with {@link GsonBuilder#setLenient()}, {@link GsonBuilder#setPrettyPrinting()} and support
	 * for Java records and additionnal {@link TypeAdapterFactory} provided with
	 * {@link #registerTypeAdapterFactory(TypeAdapterFactory)}.
	 */
	public static final Gson gsonPrettyPrinting = build(GsonBuilder::setPrettyPrinting);

	/**
	 * {@link Gson} instance with {@link GsonBuilder#setLenient()}, {@link GsonBuilder#serializeNulls()} and support for
	 * Java records and additionnal {@link TypeAdapterFactory} provided with
	 * {@link #registerTypeAdapterFactory(TypeAdapterFactory)}.
	 */
	public static final Gson gsonSerializeNulls = build(GsonBuilder::serializeNulls);

	/**
	 * {@link Gson} instance with {@link GsonBuilder#setLenient()}, {@link GsonBuilder#serializeNulls()},
	 * {@link GsonBuilder#setPrettyPrinting()} and support for Java records and additionnal {@link TypeAdapterFactory}
	 * provided with {@link #registerTypeAdapterFactory(TypeAdapterFactory)}.
	 */
	public static final Gson gsonSerializeNullsPrettyPrinting = build(b -> b.serializeNulls().setPrettyPrinting());







	private static Gson build(Function<GsonBuilder, GsonBuilder> builderModifier) {
		GsonBuilder base = new GsonBuilder()
				.registerTypeAdapterFactory(new CustomAdapterFactory())
				.setLenient();
		return builderModifier.apply(base).create();
	}


	/**
	 * Adds the provided {@link TypeAdapterFactory} to all the static Gson instances of this class.
	 * @param factory the factory to add to the
	 */
	public static void registerTypeAdapterFactory(TypeAdapterFactory factory) {
		synchronized (customTypeAdapterFactories) {
			customTypeAdapterFactories.add(factory);
		}
	}



	private static final List<TypeAdapterFactory> customTypeAdapterFactories = new ArrayList<>();



	private static class CustomAdapterFactory implements TypeAdapterFactory {
		@Override
		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
			synchronized (customTypeAdapterFactories) {
				for (TypeAdapterFactory actualFactory : customTypeAdapterFactories) {
					TypeAdapter<T> adapter = actualFactory.create(gson, type);
					if (adapter != null)
						return adapter;
				}
			}
			return null;
		}
	}







	static {
		registerTypeAdapterFactory(new RecordAdapterFactory());
	}

	// from https://github.com/google/gson/issues/1794#issuecomment-812964421
	private static class RecordAdapterFactory implements TypeAdapterFactory {
		@Override
		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
			@SuppressWarnings("unchecked")
			Class<T> clazz = (Class<T>) type.getRawType();
			if (!clazz.isRecord() || clazz == Record.class) {
				return null;
			}
			return new RecordTypeAdapter<>(gson, this, type);
		}
	}

	private static class RecordTypeAdapter<T> extends TypeAdapter<T> {
		private final Gson gson;
		private final TypeAdapterFactory factory;
		private final TypeToken<T> type;

		public RecordTypeAdapter(Gson gson, TypeAdapterFactory factory, TypeToken<T> type) {
			this.gson = gson;
			this.factory = factory;
			this.type = type;
		}

		@Override
		public void write(JsonWriter out, T value) throws IOException {
			gson.getDelegateAdapter(factory, type).write(out, value);
		}

		@Override
		public T read(JsonReader reader) throws IOException {
			if (reader.peek() == JsonToken.NULL) {
				reader.nextNull();
				return null;
			} else {
				@SuppressWarnings("unchecked")
				Class<T> clazz = (Class<T>) type.getRawType();

				RecordComponent[] recordComponents = clazz.getRecordComponents();
				Map<String, TypeToken<?>> typeMap = new HashMap<>();
				for (RecordComponent recordComponent : recordComponents) {
					typeMap.put(recordComponent.getName(), TypeToken.get(recordComponent.getGenericType()));
				}
				var argsMap = new HashMap<String, Object>();
				reader.beginObject();
				while (reader.hasNext()) {
					String name = reader.nextName();
					argsMap.put(name, gson.getAdapter(typeMap.get(name)).read(reader));
				}
				reader.endObject();

				var argTypes = new Class<?>[recordComponents.length];
				var args = new Object[recordComponents.length];
				for (int i = 0; i < recordComponents.length; i++) {
					argTypes[i] = recordComponents[i].getType();
					args[i] = argsMap.get(recordComponents[i].getName());
				}
				Constructor<T> constructor;
				try {
					constructor = clazz.getDeclaredConstructor(argTypes);
					constructor.setAccessible(true);
					return constructor.newInstance(args);
				} catch (NoSuchMethodException | InstantiationException | SecurityException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}
