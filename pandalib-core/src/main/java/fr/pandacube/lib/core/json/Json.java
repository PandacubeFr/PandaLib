package fr.pandacube.lib.core.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.Strictness;
import com.google.gson.ToNumberStrategy;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.MalformedJsonException;
import fr.pandacube.lib.core.mc_version.MinecraftVersionList.MinecraftVersionListAdapter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Provides pre-instanced {@link Gson} objects, all with support for Java records and additional
 * {@link TypeAdapterFactory} provided with {@link #registerTypeAdapterFactory(TypeAdapterFactory)}.
 */
public class Json {

	/**
	 * Makes Gson deserialize numbers to Number subclasses the same way SnakeYAML does
	 */
	private static final ToNumberStrategy YAML_EQUIVALENT_NUMBER_STRATEGY = in -> {
		String value = in.nextString();

		// YAML uses Regex to resolve values as INT or FLOAT (see org.yaml.snakeyaml.resolver.Resolver), trying FLOAT first.
		// We see in the regex that FLOAT MUST have a "." in the string, but INT must not, so we try that.
		boolean isFloat = value.contains(".");

		if (isFloat) {
			// if is float, will only parse to Double
			//   (see org.yaml.snakeyaml.constructor.SafeConstructor.ConstructYamlFloat)
			try {
				Double d = Double.valueOf(value);
				if ((d.isInfinite() || d.isNaN()) && !in.isLenient()) {
					throw new MalformedJsonException("JSON forbids NaN and infinities: " + d + "; at path " + in.getPreviousPath());
				}
				return d;
			} catch (NumberFormatException e) {
				throw new JsonParseException("Cannot parse " + value + "; at path " + in.getPreviousPath(), e);
			}
		}
		else {
			// if integer, will try to parse int, then long, then BigDecimal
			//   (see org.yaml.snakeyaml.constructor.SafeConstructor.ConstructYamlInt
			//    then org.yaml.snakeyaml.constructor.SafeConstructor.createNumber)
			try {
				return Integer.valueOf(value);
			} catch (NumberFormatException e) {
				try {
					return Long.valueOf(value);
				} catch (NumberFormatException e2) {
					try {
						return new BigInteger(value);
					} catch (NumberFormatException e3) {
						throw new JsonParseException("Cannot parse " + value + "; at path " + in.getPreviousPath(), e3);
					}
				}
			}
		}
	};




	/**
	 * {@link Gson} instance with {@link GsonBuilder#setLenient()} and support for Java records and additional
	 * {@link TypeAdapterFactory} provided with {@link #registerTypeAdapterFactory(TypeAdapterFactory)}.
	 */
	public static final Gson gson = build(Function.identity());

	/**
	 * {@link Gson} instance with {@link Strictness#LENIENT}, {@link GsonBuilder#setPrettyPrinting()} and support
	 * for Java records and additional {@link TypeAdapterFactory} provided with
	 * {@link #registerTypeAdapterFactory(TypeAdapterFactory)}.
	 */
	public static final Gson gsonPrettyPrinting = build(GsonBuilder::setPrettyPrinting);

	/**
	 * {@link Gson} instance with {@link Strictness#LENIENT}, {@link GsonBuilder#serializeNulls()} and support for
	 * Java records and additional {@link TypeAdapterFactory} provided with
	 * {@link #registerTypeAdapterFactory(TypeAdapterFactory)}.
	 */
	public static final Gson gsonSerializeNulls = build(GsonBuilder::serializeNulls);

	/**
	 * {@link Gson} instance with {@link Strictness#LENIENT}, {@link GsonBuilder#serializeNulls()},
	 * {@link GsonBuilder#setPrettyPrinting()} and support for Java records and additional {@link TypeAdapterFactory}
	 * provided with {@link #registerTypeAdapterFactory(TypeAdapterFactory)}.
	 */
	public static final Gson gsonSerializeNullsPrettyPrinting = build(b -> b.serializeNulls().setPrettyPrinting());







	private static Gson build(Function<GsonBuilder, GsonBuilder> builderModifier) {
		GsonBuilder base = new GsonBuilder()
				.registerTypeAdapterFactory(new CustomAdapterFactory())
				.disableHtmlEscaping()
				.setObjectToNumberStrategy(YAML_EQUIVALENT_NUMBER_STRATEGY)
				.setStrictness(Strictness.LENIENT);
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
		registerTypeAdapterFactory(StackTraceElementAdapter.FACTORY);
		registerTypeAdapterFactory(ThrowableAdapter.FACTORY);
		registerTypeAdapterFactory(MinecraftVersionListAdapter.FACTORY);
	}


	/*public static void main(String[] args) {
		TypeToken<Map<String, Object>> MAP_STR_OBJ_TYPE = new TypeToken<>() { };
		Map<String, Object> map = gson.fromJson("{" +
				"\"int\":34," +
				"\"long\":3272567356876864," +
				"\"bigint\":-737868677777837833757846576245765," +
				"\"float\":34.0" +
				"}", MAP_STR_OBJ_TYPE.getType());
		for (String key : map.keySet()) {
			Object v = map.get(key);
			System.out.println(key + ": " + v + " (type " + v.getClass() + ")");
		}
	}*/

	private Json() {}

}
