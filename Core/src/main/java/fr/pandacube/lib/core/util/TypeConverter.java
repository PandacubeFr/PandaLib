package fr.pandacube.lib.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * Utility for conversion of basic Java types and JsonElements types
 * @author Marc
 *
 */
public class TypeConverter {
	
	
	public static Integer toInteger(Object o) {
		if (o == null) {
			return null;
		}
		
		if (o instanceof JsonElement) {
			try {
				return ((JsonElement)o).getAsInt();
			} catch(UnsupportedOperationException e) {
				throw new ConvertionException(e);
			}
		}
		
		if (o instanceof Number) {
			return ((Number)o).intValue();
		}
		if (o instanceof String) {
			try {
			return Integer.parseInt((String)o);
			} catch (NumberFormatException e) {
				throw new ConvertionException(e);
			}
		}
		if (o instanceof Boolean) {
			return ((Boolean)o) ? 1 : 0;
		}
		
		throw new ConvertionException("No integer convertion available for an instance of "+o.getClass());
	}
	
	public static int toPrimInt(Object o) {
		Integer val = toInteger(o);
		if (val == null)
			throw new ConvertionException("null values can't be converted to primitive int");
		return val;
	}
	
	public static Double toDouble(Object o) {
		if (o == null) {
			return null;
		}
		
		if (o instanceof JsonElement) {
			try {
			return ((JsonElement)o).getAsDouble();
			} catch(UnsupportedOperationException e) {
				throw new ConvertionException(e);
			}
		}

		if (o instanceof Number) {
			return ((Number)o).doubleValue();
		}
		if (o instanceof String) {
			try {
			return Double.parseDouble((String)o);
			} catch (NumberFormatException e) {
				throw new ConvertionException(e);
			}
		}
		if (o instanceof Boolean) {
			return ((Boolean)o) ? 1d : 0d;
		}
		
		throw new ConvertionException("No double convertion available for an instance of "+o.getClass());
		
	}
	
	public static double toPrimDouble(Object o) {
		Double val = toDouble(o);
		if (val == null)
			throw new ConvertionException("null values can't converted to primitive int");
		return val;
	}
	
	public static String toString(Object o) {
		if (o == null) {
			return null;
		}
		
		if (o instanceof JsonElement) {
			try {
				return ((JsonElement)o).getAsString();
			} catch(UnsupportedOperationException e) {
				throw new ConvertionException(e);
			}
		}
		
		if (o instanceof Number || o instanceof String || o instanceof Boolean || o instanceof Character) {
			return o.toString();
		}

		throw new ConvertionException("No string convertion available for an instance of "+o.getClass());
		
	}
	
	/**
	 * 
	 * @param o the object to convert to good type
	 * @param mapIntKeys if the String key representing an int should be duplicated as integer type,
	 * which map to the same value as the original String key. For example, if a key is "12" and map
	 * to the object <i>o</i>, an integer key 12 will be added and map to the same object 
	 * <i>o</i>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<Object, Object> toMap(Object o, boolean mapIntKeys) {
		if (o == null) {
			return null;
		}
		
		if (o instanceof JsonElement) {
			o = new Gson().fromJson((JsonElement)o, Object.class);
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
						} catch (NumberFormatException e) { }
					}
				}
				if (!newEntries.isEmpty()) {
					currMap = new HashMap<>(currMap);
					currMap.putAll(newEntries);
				}
			}
			return currMap;
		}
		
		if (o instanceof List) {
			List<?> list = (List<?>) o;
			Map<Object, Object> map = new HashMap<>();
			for(int i = 0; i < list.size(); i++) {
				map.put(Integer.toString(i), list.get(i));
				map.put(i, list.get(i));
			}
			return map;
		}
		

		throw new ConvertionException("No Map convertion available for an instance of "+o.getClass());
		
		
	}
	
	
	@SuppressWarnings("unchecked")
	public static List<Object> toList(Object o) {
		if (o == null) {
			return null;
		}
		
		if (o instanceof JsonElement) {
			o = new Gson().fromJson((JsonElement)o, Object.class);
		}
		

		
		if (o instanceof List) {
			return (List<Object>) o;
		}
		
		if (o instanceof Map) {
			return new ArrayList<>(((Map<?, ?>)o).values());
		}
		

		throw new ConvertionException("No Map convertion available for an instance of "+o.getClass());
		
		
		
	}
	
	
	
	
	
	
	public static class ConvertionException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public ConvertionException(String m) {
			super(m);
		}
		public ConvertionException(Throwable t) {
			super(t);
		}
		public ConvertionException(String m, Throwable t) {
			super(m, t);
		}
		
	}
	
}
