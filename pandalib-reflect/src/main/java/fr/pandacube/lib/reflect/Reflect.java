package fr.pandacube.lib.reflect;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides methods to get instances of {@link ReflectClass}.
 */
public class Reflect {


	private static final Map<Class<?>, ReflectClass<?>> classCache = Collections.synchronizedMap(new HashMap<>());


	/**
	 * Wraps the provided class into a {@link ReflectClass}.
	 * @param clazz the class to wrap.
	 * @param <T> the type of the class.
	 * @return a {@link ReflectClass} wrapping the provided class.
	 */
    @SuppressWarnings("unchecked")
	public static <T> ReflectClass<T> ofClass(Class<T> clazz) {
    	return (ReflectClass<T>) classCache.computeIfAbsent(clazz, ReflectClass::new);
    }

	/**
	 * Wraps the provided class into a {@link ReflectClass}.
	 * @param className the name of the class, passed into {@link Class#forName(String)} before using
	 *                  {@link #ofClass(Class)}.
	 * @return a {@link ReflectClass} wrapping the provided class.
	 * @throws ClassNotFoundException if the provided class was not found.
	 */
    public static ReflectClass<?> ofClass(String className) throws ClassNotFoundException {
    	return ofClass(ReflectionWrapperBypass.getClass(className));
    }

	/**
	 * Wraps the class of the provided object into a {@link ReflectClass}.
	 * @param instance the object from which to get the class using {@link Object#getClass()}.
	 * @return a {@link ReflectClass} wrapping the provided object’s class.
	 * @throws IllegalArgumentException if {@code instance} is null.
	 */
    public static ReflectClass<?> ofClassOfInstance(Object instance) {
		if (instance == null)
			throw new IllegalArgumentException("instance can't be null");
    	return ofClass(instance.getClass());
    }


	private Reflect() {}
}
