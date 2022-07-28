package fr.pandacube.lib.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassGraphException;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

/**
 * Wrapper of a {@link Class} with less verbose access to methods and properties.
 * @param <T> the type of the wrapped class.
 */
public class ReflectClass<T> {
    private final Class<T> clazz;

    private final Map<MethodIdentifier, ReflectMethod<T>> methodCache = Collections.synchronizedMap(new HashMap<>());
    private final Map<ConstructorIdentifier, ReflectConstructor<T>> constructorCache = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, ReflectField<T>> fieldCache = Collections.synchronizedMap(new HashMap<>());

    ReflectClass(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Returns the class wrapped by this {@link ReflectClass} instance.
     * @return the class wrapped by this {@link ReflectClass} instance.
     */
    public Class<T> get() {
        return clazz;
    }







    ReflectMethod<T> method(MethodIdentifier key, boolean bypassFilter) throws NoSuchMethodException {
        ReflectMethod<T> method = methodCache.get(key);
        if (method == null) {
            method = new ReflectMethod<>(this, key, bypassFilter);
            methodCache.put(key, method);
        }
        return method;
    }

    /**
     * Provides a {@link ReflectMethod} wrapping the requested {@link Method}.
     * @param name the method name.
     * @param paramTypes the types of the method parameters.
     * @return a {@link ReflectMethod} wrapping the requested {@link Method}.
     * @throws NoSuchMethodException if the requested method doesn’t exists in the wrapped class.
     */
    public ReflectMethod<T> method(String name, Class<?>... paramTypes) throws NoSuchMethodException {
        return method(new MethodIdentifier(name, paramTypes), false);
    }

    /**
     * Provides a {@link ReflectMethod} wrapping the requested {@link Method}, bypassing some internal filtering in the
     * {@link Class} implementation.
     * @param name the method name.
     * @param paramTypes the types of the method parameters.
     * @return a {@link ReflectMethod} wrapping the requested {@link Method}.
     * @throws NoSuchMethodException if the requested method doesn’t exists in the wrapped class.
     */
    public ReflectMethod<T> filteredMethod(String name, Class<?>... paramTypes) throws NoSuchMethodException {
        return method(new MethodIdentifier(name, paramTypes), true);
    }






    private ReflectConstructor<T> constructor(ConstructorIdentifier key, boolean bypassFilter) throws NoSuchMethodException {
        ReflectConstructor<T> constructor = constructorCache.get(key);
        if (constructor == null) {
            constructor = new ReflectConstructor<>(this, key, bypassFilter);
            constructorCache.put(key, constructor);
        }
        return constructor;
    }

    /**
     * Provides a {@link ReflectConstructor} wrapping the requested {@link Constructor}.
     * @param paramTypes the types of the constructor parameters.
     * @return a {@link ReflectConstructor} wrapping the requested {@link Constructor}.
     * @throws NoSuchMethodException if the requested constructor doesn’t exists in the wrapped class.
     */
    public ReflectConstructor<T> constructor(Class<?>... paramTypes) throws NoSuchMethodException {
        return constructor(new ConstructorIdentifier(paramTypes), false);
    }

    /**
     * Provides a {@link ReflectConstructor} wrapping the requested {@link Constructor}, bypassing some internal
     * filtering in the {@link Class} implementation.
     * @param paramTypes the types of the constructor parameters.
     * @return a {@link ReflectConstructor} wrapping the requested {@link Constructor}.
     * @throws NoSuchMethodException if the requested constructor doesn’t exists in the wrapped class.
     */
    public ReflectConstructor<T> filteredConstructor(Class<?>... paramTypes) throws NoSuchMethodException {
        return constructor(new ConstructorIdentifier(paramTypes), true);
    }






    private ReflectField<T> field0(String name, boolean bypassFilter) throws NoSuchFieldException {
        ReflectField<T> constructor = fieldCache.get(name);
        if (constructor == null) {
            constructor = new ReflectField<>(this, name, bypassFilter);
            fieldCache.put(name, constructor);
        }
        return constructor;
    }

    /**
     * Provides a {@link ReflectField} wrapping the requested {@link Field}.
     * @param name the name of the field.
     * @return a {@link ReflectField} wrapping the requested {@link Field}.
     * @throws NoSuchFieldException if the requested field doesn’t exists in the wrapped class.
     */
    public ReflectField<T> field(String name) throws NoSuchFieldException {
        return field0(name, false);
    }

    /**
     * Provides a {@link ReflectField} wrapping the requested {@link Field}, bypassing some internal filtering in the
     * {@link Class} implementation.
     * @param name the name of the field.
     * @return a {@link ReflectField} wrapping the requested {@link Field}.
     * @throws NoSuchFieldException if the requested field doesn’t exists in the wrapped class.
     * @deprecated on Java 17, does not work due to module encapsulation, it is impossible to bypass the Java reflection
     *             API internal filtering.
     */
    @Deprecated(since = "Java 17")
    public ReflectField<T> filteredField(String name) throws NoSuchFieldException {
        return field0(name, true);
    }









    private final AtomicReference<List<Class<?>>> cachedSubclasses = new AtomicReference<>();

    /**
     * Get all subclasses of the current class, using the ClassGraph library.
     * <p>
     * If the returned list is not yet cached, or {@code forceUpdateCache} is true, then the cache is updated before
     * being returned. This may take some time.
     * <p>
     * The ClassGraph library scan all class files in the class path, then loads those which will be returned by
     * this method.
     *
     * @param forceUpdateCache to force the update of the cache, even if it already filled.
     * @return the list of all subclasses found in all loaded class loader.
     * @throws ClassGraphException      if any of the worker threads throws an uncaught exception, or the scan was interrupted. (see {@link ClassGraph#scan})
     * @throws IllegalArgumentException f an exception or error was thrown while trying to load any of the classes. (see {@link ClassInfoList#loadClasses()})
     */
    public List<Class<? extends T>> getAllSubclasses(boolean forceUpdateCache) {
        synchronized (cachedSubclasses) {
            if (forceUpdateCache || cachedSubclasses.get() == null) {
                try (ScanResult scanResult = new ClassGraph().enableClassInfo().ignoreClassVisibility().scan()) {
                    cachedSubclasses.set(scanResult.getSubclasses(clazz).loadClasses());
                }
            }
            @SuppressWarnings("unchecked")
            List<Class<? extends T>> ret = (List<Class<? extends T>>) (List<?>) cachedSubclasses.get();
            return ret;
        }

    }
}
