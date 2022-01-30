package fr.pandacube.lib.core.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import sun.misc.Unsafe;

public class Reflect {
    
    private static final Unsafe sunMiscUnsafeInstance;

    static {
    	classCache = Collections.synchronizedMap(new HashMap<>());

        try {
            sunMiscUnsafeInstance = (Unsafe) ofClass(Unsafe.class).field("theUnsafe")
            		.getStaticValue();
        } catch (Exception e) {
            throw new RuntimeException("Cannot access to " + Unsafe.class + ".theUnsafe value.", e);
        }
    }
    
    
    
    
    
    
    
    
    private static final Map<Class<?>, ReflectClass<?>> classCache;
    
    @SuppressWarnings("unchecked")
	public static <T> ReflectClass<T> ofClass(Class<T> clazz) {
    	return (ReflectClass<T>) classCache.computeIfAbsent(clazz, ReflectClass::new);
    }
    
    public static ReflectClass<?> ofClass(String className) throws ClassNotFoundException {
    	return ofClass(Class.forName(className));
    }
    
    public static ReflectClass<?> ofClassOfInstance(Object instance) {
		if (instance == null)
			throw new IllegalArgumentException("instance can't be null");
    	return ofClass(instance.getClass());
    }
    
    
    
    
    
    
    
    
    private record MethodIdentifier(String methodName, Class<?>[] parameters) {
    	private MethodIdentifier {
    		Objects.requireNonNull(methodName);
    		parameters = (parameters == null) ? new Class<?>[0] : parameters;
		}
    	@Override
    	public boolean equals(Object other) {
			return other != null && other instanceof MethodIdentifier o
					&& o.methodName.equals(methodName)
					&& Arrays.equals(o.parameters, parameters);
		}
    	@Override
    	public int hashCode() {
    		return methodName.hashCode() ^ Arrays.hashCode(parameters);
    	}
    }
    
    private record ConstructorIdentifier(Class<?>[] parameters) {
    	private ConstructorIdentifier {
    		parameters = (parameters == null) ? new Class<?>[0] : parameters;
		}
    	@Override
    	public boolean equals(Object other) {
			return other != null && other instanceof ConstructorIdentifier o
					&& Arrays.equals(o.parameters, parameters);
		}
    	@Override
    	public int hashCode() {
    		return Arrays.hashCode(parameters);
    	}
    }
    
    
    
    
    
    
    public static class ReflectClass<T> {
    	private Class<T> clazz;

        private final Map<MethodIdentifier, ReflectMethod<T>> methodCache = Collections.synchronizedMap(new HashMap<>());
        private final Map<ConstructorIdentifier, ReflectConstructor<T>> constructorCache = Collections.synchronizedMap(new HashMap<>());
        private final Map<String, ReflectField<T>> fieldCache = Collections.synchronizedMap(new HashMap<>());
    	
    	private ReflectClass(Class<T> clazz) {
    		this.clazz = clazz;
		}
    	
    	private ReflectMethod<T> method(MethodIdentifier key) {
    		return methodCache.computeIfAbsent(key, k -> new ReflectMethod<>(this, k));
    	}
    	
    	public ReflectMethod<T> method(String name, Class<?>... paramTypes) {
    		return method(new MethodIdentifier(name, paramTypes));
    	}
    	
    	private ReflectConstructor<T> constructor(ConstructorIdentifier key) {
    		return constructorCache.computeIfAbsent(key, k -> new ReflectConstructor<>(this, k));
    	}
    	
    	public ReflectConstructor<T> constructor(Class<?>... paramTypes) {
    		return constructor(new ConstructorIdentifier(paramTypes));
    	}
    	
    	public ReflectField<T> field(String name) {
    		return fieldCache.computeIfAbsent(name, n -> new ReflectField<>(this, n));
    	}
    }
    
    
    
    
    
    
    
    
    
    

    
    
    
    
    
    
	
	
	
	
	
	
	public static abstract class ReflectClassEl<T> {
		ReflectClass<T> reflectClass;
		
		protected ReflectClassEl(ReflectClass<T> c) {
			reflectClass = c;
		}
		
	}
	
	public static class ReflectField<T> extends ReflectClassEl<T> {
		String elementName;
		
		private Field cached, cachedFiltered;
		
		/* package */ ReflectField(ReflectClass<T> c, String name) {
			super(c);
			elementName = name;
		}
		
		public synchronized Field get() throws NoSuchFieldException {
			if (cached == null) {
				cached = getDeclaredRecursively(reflectClass.clazz,
					c -> c.getDeclaredField(elementName),
					c -> ofClass(c).field(elementName).get(),
					f -> f.setAccessible(true));
			}
			return cached;
		}
		
		/* package */ synchronized Field getFiltered() throws NoSuchFieldException {
			if (cachedFiltered == null) {
				cachedFiltered = Reflect.getFiltered(reflectClass.clazz,
						c -> c.getDeclaredField(elementName),
						f -> f.setAccessible(true),
						f -> elementName.equals(f.getName()),
						"getDeclaredFields0", "copyField");
			}
			return cachedFiltered;
		}
		
		public Object getValue(Object instance) throws ReflectiveOperationException {
			return get().get(instance);
		}
		
		public Object getStaticValue() throws ReflectiveOperationException {
			return getValue(null);
		}
		
		public void setValue(Object instance, Object value) throws ReflectiveOperationException {
			
			Field f = get();
			if (Modifier.isFinal(f.getModifiers())) {
				// if the field is final, we have to do some unsafe stuff :/
				if (sunMiscUnsafeInstance != null) { // Java >= 16
					// set the value of the field, directly in the memory
				    if (Modifier.isStatic(f.getModifiers())) {
						long offset = sunMiscUnsafeInstance.staticFieldOffset(f);
						sunMiscUnsafeInstance.putObject(sunMiscUnsafeInstance.staticFieldBase(f), offset, value);
					} else {
						long offset = sunMiscUnsafeInstance.objectFieldOffset(f);
						sunMiscUnsafeInstance.putObject(instance, offset, value);
					}
				} else { // Java < 16
					// change the modifier in the Field instance so the method #set(instance, value) doesn’t throw an exception
				    int modifiers = f.getModifiers();
					if (Modifier.isFinal(modifiers)) {
						ofClass(Field.class).field("modifiers").getFiltered().set(f, modifiers & ~Modifier.FINAL);
					}
				    f.set(instance, value);
				}
			}
			else { // not final value
				f.set(instance, value);
			}
		}
		
		public void setStaticValue(Object value) throws ReflectiveOperationException {
			setValue(null, value);
		}
		
		
		
	}
	
	public static class ReflectMethod<T> extends ReflectClassEl<T> {
		String elementName;
		
		private Method cached, cachedFiltered;
		
		MethodIdentifier methodId;
		Class<?>[] parameterTypes;
		
		/* package */ ReflectMethod(ReflectClass<T> c, MethodIdentifier methodId) {
			super(c);
			this.elementName = methodId.methodName;
			this.methodId = methodId;
			parameterTypes = methodId.parameters;
		}
		
		
		public Method get() throws NoSuchMethodException {
			if (cached == null) {
				cached = getDeclaredRecursively(reflectClass.clazz,
					c -> c.getDeclaredMethod(elementName, parameterTypes),
					c -> ofClass(c).method(methodId).get(),
					m -> m.setAccessible(true));
			}
			return cached;
		}
		
		/* package */ Method getFiltered() throws NoSuchMethodException {
			if (cachedFiltered == null) {
				cachedFiltered = Reflect.getFiltered(reflectClass.clazz,
					c -> c.getDeclaredMethod(elementName, parameterTypes),
					m -> m.setAccessible(true),
					m -> elementName.equals(m.getName()) && Arrays.equals(parameterTypes, m.getParameterTypes()),
					"getDeclaredMethods0", "copyMethod");
			}
			return cachedFiltered;
		}
		
		public Object invoke(Object instance, Object... values) throws ReflectiveOperationException {
			return get().invoke(instance, values);
		}
		
		public Object invokeStatic(Object... values) throws ReflectiveOperationException {
			return invoke(null, values);
		}
	}
	
	public static class ReflectConstructor<T> extends ReflectClassEl<T> {
		
		private Constructor<T> cached, cachedFiltered;
		
		ConstructorIdentifier constructorId;
		Class<?>[] parameterTypes;
		
		/* package */ ReflectConstructor(ReflectClass<T> c, ConstructorIdentifier constructorId) {
			super(c);
			this.constructorId = constructorId;
			parameterTypes = constructorId.parameters;
		}
		
		
		public Constructor<T> get() throws NoSuchMethodException {
			if (cached == null) {
				Constructor<T> el = null;
				el = reflectClass.clazz.getDeclaredConstructor(parameterTypes);
				el.setAccessible(true);
				cached = el;
			}
			return cached;
		}
		
		/* package */ Constructor<T> getFiltered() throws NoSuchMethodException {
			if (cachedFiltered == null) {
				cachedFiltered = Reflect.getFiltered(reflectClass.clazz,
					c -> c.getDeclaredConstructor(parameterTypes),
					m -> m.setAccessible(true),
					m -> Arrays.equals(parameterTypes, m.getParameterTypes()),
					"getDeclaredConstructors0", "copyConstructor");
			}
			return cachedFiltered;
		}
		
		public T instanciate(Object... values) throws ReflectiveOperationException {
			return get().newInstance(values);
		}
	}
	
	
	
	
	
	
	
	
	
	


    private interface GetUncheckedClassReflectiveElement<T, E extends ReflectiveOperationException> {
    	public T get(Class<?> clazz) throws E;
    }
    
    private interface GetReflectiveElement<C, T, E extends ReflectiveOperationException> {
    	public T get(Class<C> clazz) throws E;
    }
    
    private static <C, T, E extends ReflectiveOperationException> T getDeclaredRecursively(
    		Class<C> clazz, GetReflectiveElement<C, T, E> jlrGetter,
    		GetUncheckedClassReflectiveElement<T, E> parentGetter, Consumer<T> setAccessible) throws E {
    	Objects.requireNonNull(clazz, "Class instance not provided");

    	E ex = null;
    	T el = null;
    	
    	// get element in provided class
    	try {
			el = jlrGetter.get(clazz);
	    	setAccessible.accept(el);
		} catch (ReflectiveOperationException e) {
			@SuppressWarnings("unchecked")
			E ee = (E) e;
			ex = ee;
		}
    	
    	// get element in parent class (will do recursion)
    	if (el == null) {
    		try {
    			el = parentGetter.get(clazz.getSuperclass());
    		} catch (ReflectiveOperationException e) {
    			if (ex == null) {
    				@SuppressWarnings("unchecked")
    				E ee = (E) e;
    				ex = ee;
    			}
    			else {
    				ex.addSuppressed(e);
    			}
    		}
    	}
    	
    	if (el == null)
    		throw ex;
    	
    	return el;
    }
    
    
    
    /**
     * Get a Field or Method of a class that is not accessible using {@link Class#getDeclaredField(String)}
     * or using {@link Class#getDeclaredMethod(String, Class...)} because the implementation of {@link Class}
     * block its direct access.
     * 
     * This method calls an internal method of {@link Class} to retrieve the full list of field or method, then
     * search in the list for the requested element.
     */
    private static <C, T, E extends ReflectiveOperationException> T getFiltered(
    		Class<C> clazz, GetReflectiveElement<C, T, E> jlrGetter,
    		Consumer<T> setAccessible, Predicate<T> elementChecker,
    		String privateMethodName, String copyMethodName) throws E {
    	Objects.requireNonNull(clazz, "Class instance not provided");

		T el = null;
		
    	E ex = null;
		try {
			el = jlrGetter.get(clazz);
		} catch (ReflectiveOperationException e) {
			@SuppressWarnings("unchecked")
			E ee = (E) e;
			ex = ee;
		}
		
		try {
			@SuppressWarnings("unchecked")
			T[] elements = (T[]) ofClassOfInstance(clazz).method(privateMethodName, boolean.class).invoke(clazz, false);
			for (T element : elements) {
        		if (elementChecker.test(element)) {
        			// the values in the elements array have to be copied
        			// (using special private methods in reflection api) before using it
        			Object reflectionFactoryOfClazz = ofClassOfInstance(clazz).method("getReflectionFactory").invoke(clazz);
        			@SuppressWarnings("unchecked")
					T copiedElement = (T) ofClassOfInstance(reflectionFactoryOfClazz)
							.method(copyMethodName, element.getClass())
							.invoke(reflectionFactoryOfClazz, element);
        			el = copiedElement;
                    break;
                }
	        }
		} catch (ReflectiveOperationException e) {
			if (ex != null)
				ex.addSuppressed(e);
		}
        
		
		if (el == null)
			throw ex;
		
		setAccessible.accept(el);
		
		return el;
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static Cache<Class<?>, List<Class<?>>> subClassesLists = CacheBuilder.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build();
	
	public static <E> List<Class<? extends E>> getAllSubclasses(Class<E> clazz) {
		try {
			@SuppressWarnings("unchecked")
			List<Class<? extends E>> classes = (List<Class<? extends E>>) (List<?>) subClassesLists.get(clazz, () -> {
				try (ScanResult scanResult = new ClassGraph().enableClassInfo().ignoreClassVisibility().scan()) {
					return scanResult.getSubclasses(clazz.getName()).loadClasses();
				}
			});
			return classes;
		} catch(ExecutionException e) {
			Log.severe(e);
			return null;
		}
		
	}
	
	
	

}
