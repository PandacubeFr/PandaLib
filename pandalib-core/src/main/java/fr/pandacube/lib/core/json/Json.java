package fr.pandacube.lib.core.json;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

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



	private static boolean hasGsonNativeRecordSupport() {
		for (Class<?> innerClasses : ReflectiveTypeAdapterFactory.class.getDeclaredClasses()) {
			if (innerClasses.getSimpleName().equals("RecordAdapter"))
				return true;
		}
		return false;
	}



	static {
		if (!hasGsonNativeRecordSupport())
			registerTypeAdapterFactory(RecordTypeAdapter.FACTORY);
	}

}
