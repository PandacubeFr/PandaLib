package fr.pandacube.lib.core.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;

/**
 * Provides conversion between Java types and {@link JsonElement} types.
 */
public class TypeConverter {

	/**
	 * Converts the provided object to an {@link Integer}.
	 * @param o the object to convert.
	 * @return the object converted to an {@link Integer}.
	 * @throws ConversionException is a conversion error occurs.
	 */
	public static Integer toInteger(Object o) {
		if (o == null) {
			return null;
		}
		
		if (o instanceof JsonElement) {
			try {
				return ((JsonElement)o).getAsInt();
			} catch(UnsupportedOperationException e) {
				throw new ConversionException(e);
			}
		}
		
		if (o instanceof Number) {
			return ((Number)o).intValue();
		}
		if (o instanceof String) {
			try {
			return Integer.parseInt((String)o);
			} catch (NumberFormatException e) {
				throw new ConversionException(e);
			}
		}
		if (o instanceof Boolean) {
			return ((Boolean)o) ? 1 : 0;
		}
		
		throw new ConversionException("No integer conversion available for an instance of "+o.getClass());
	}

	/**
	 * Converts the provided object to a primitive int.
	 * @param o the object to convert.
	 * @return the object converted to a primitive int.
	 * @throws ConversionException is a conversion error occurs.
	 */
	public static int toPrimInt(Object o) {
		Integer val = toInteger(o);
		if (val == null)
			throw new ConversionException("null values can't be converted to primitive int");
		return val;
	}

	/**
	 * Converts the provided object to a {@link Double}.
	 * @param o the object to convert.
	 * @return the object converted to a {@link Double}.
	 * @throws ConversionException is a conversion error occurs.
	 */
	public static Double toDouble(Object o) {
		if (o == null) {
			return null;
		}
		
		if (o instanceof JsonElement) {
			try {
			return ((JsonElement)o).getAsDouble();
			} catch(UnsupportedOperationException e) {
				throw new ConversionException(e);
			}
		}

		if (o instanceof Number) {
			return ((Number)o).doubleValue();
		}
		if (o instanceof String) {
			try {
			return Double.parseDouble((String)o);
			} catch (NumberFormatException e) {
				throw new ConversionException(e);
			}
		}
		if (o instanceof Boolean) {
			return ((Boolean)o) ? 1d : 0d;
		}
		
		throw new ConversionException("No double conversion available for an instance of "+o.getClass());
		
	}

	/**
	 * Converts the provided object to a primitive double.
	 * @param o the object to convert.
	 * @return the object converted to a primitive double.
	 * @throws ConversionException is a conversion error occurs.
	 */
	public static double toPrimDouble(Object o) {
		Double val = toDouble(o);
		if (val == null)
			throw new ConversionException("null values can't converted to primitive int");
		return val;
	}

	/**
	 * Converts the provided object to a {@link String}.
	 * @param o the object to convert.
	 * @return the object converted to a {@link String}.
	 * @throws ConversionException is a conversion error occurs.
	 */
	public static String toString(Object o) {
		if (o == null) {
			return null;
		}
		
		if (o instanceof JsonElement) {
			try {
				return ((JsonElement)o).getAsString();
			} catch(UnsupportedOperationException e) {
				throw new ConversionException(e);
			}
		}
		
		if (o instanceof Number || o instanceof String || o instanceof Boolean || o instanceof Character) {
			return o.toString();
		}

		throw new ConversionException("No string conversion available for an instance of "+o.getClass());
		
	}
	
	/**
	 * Converts the provided object to a {@link Map}.
	 * @param o the object to convert.
	 * @param mapIntKeys if the String key representing an int should be duplicated as integer type,
	 * which map to the same value as the original String key. For example, if a key is "12" and map
	 * to the object <i>o</i>, an integer key 12 will be added and map to the same object <i>o</i>.
	 * @return the object converted to a {@link Map}.
	 * @throws ConversionException is a conversion error occurs.
	 */
	@SuppressWarnings("unchecked")
	public static Map<Object, Object> toMap(Object o, boolean mapIntKeys) {
		if (o == null) {
			return null;
		}
		
		if (o instanceof JsonElement) {
			o = Json.gson.fromJson((JsonElement)o, Object.class);
		}
		
		if (o instanceof Map) {
			Map<Object, Object> currMap = (Map<Object, Object>) o;
			if (mapIntKeys) {
				Map<Integer, Object> newEntries = new HashMap<>();
				for (Map.Entry<Object, Object> entry : currMap.entrySet()) {
					if (entry.getKey() instanceof String) {
						try {
							int intKey = Integer.parseInt((String)entry.getKey());
							newEntries.put(intKey, entry.getValue());
						} catch (NumberFormatException ignored) { }
					}
				}
				if (!newEntries.isEmpty()) {
					currMap = new HashMap<>(currMap);
					currMap.putAll(newEntries);
				}
			}
			return currMap;
		}
		
		if (o instanceof List<?> list) {
			Map<Object, Object> map = new HashMap<>();
			for(int i = 0; i < list.size(); i++) {
				map.put(Integer.toString(i), list.get(i));
				map.put(i, list.get(i));
			}
			return map;
		}

		throw new ConversionException("No Map conversion available for an instance of "+o.getClass());
	}


	/**
	 * Converts the provided object to a {@link List}.
	 * @param o the object to convert.
	 * @return the object converted to a {@link List}.
	 * @throws ConversionException is a conversion error occurs.
	 */
	@SuppressWarnings("unchecked")
	public static List<Object> toList(Object o) {
		if (o == null) {
			return null;
		}
		
		if (o instanceof JsonElement) {
			o = Json.gson.fromJson((JsonElement)o, Object.class);
		}
		

		
		if (o instanceof List) {
			return (List<Object>) o;
		}
		
		if (o instanceof Map) {
			return new ArrayList<>(((Map<?, ?>)o).values());
		}
		

		throw new ConversionException("No Map conversion available for an instance of "+o.getClass());
		
		
		
	}


	/**
	 * Thrown when a conversion error occurs.
	 */
	public static class ConversionException extends RuntimeException {
		
		private ConversionException(String m) {
			super(m);
		}
		private ConversionException(Throwable t) {
			super(t);
		}
		
	}
	
}
