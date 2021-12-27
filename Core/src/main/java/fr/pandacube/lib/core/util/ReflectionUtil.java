package fr.pandacube.lib.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import sun.misc.Unsafe;

public class ReflectionUtil {
    
    private record MethodCacheKey(Class<?> clazz, String methodName, List<Class<?>> parameters) { }
    private static final Map<MethodCacheKey, Method> methodCache;

    private record FieldCacheKey(Class<?> clazz, String fieldName) { };
    private static final Map<FieldCacheKey, Field> fieldCache;
    
    private static final Unsafe sunMiscUnsafeInstance;

    static {
    	methodCache = new HashMap<>();
    	fieldCache = new HashMap<>();

        try {
            sunMiscUnsafeInstance = (Unsafe) field("theUnsafe")
            		.inClass(Unsafe.class)
            		.getStaticValue();
        } catch (Exception e) {
            throw new RuntimeException("Cannot access to " + Unsafe.class + ".theUnsafe value.", e);
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    

	
	public static MethodReflectionBuilder method(String name, Class<?>... paramTypes) {
		return new MethodReflectionBuilder(name, paramTypes);
	}
	
	public static FieldReflectionBuilder field(String name) {
		return new FieldReflectionBuilder(name);
	}
    
    
    
    
    
    
	
	
	
	
	
	
	public static abstract class ReflectionBuilder {
		Class<?> clazz;
		String elementName;
		
		protected ReflectionBuilder(String elementName) {
			this.elementName = elementName;
		}

		public ReflectionBuilder inClass(Class<?> c) {
			clazz = c;
			return this;
		}
		public ReflectionBuilder inClass(String className) throws ClassNotFoundException {
			return inClass(Class.forName(className));
		}
		public ReflectionBuilder inClassOfInstance(Object inst) {
			if (inst == null)
				throw new IllegalArgumentException("instance can't be null");
			return inClass(inst.getClass());
		}
		
	}
	
	public static class FieldReflectionBuilder extends ReflectionBuilder {
		
		/* package */ FieldReflectionBuilder(String name) {
			super(name);
		}
		
		// override method so their return type is more specific
		public FieldReflectionBuilder inClass(Class<?> c) { return (FieldReflectionBuilder) super.inClass(c); }
		public FieldReflectionBuilder inClass(String className) throws ClassNotFoundException { return (FieldReflectionBuilder) super.inClass(className); }
		public FieldReflectionBuilder inClassOfInstance(Object inst) { return (FieldReflectionBuilder) super.inClassOfInstance(inst); }
		
		public Field get() throws NoSuchFieldException {
			return getDeclaredRecursively(clazz, elementName,
					c -> c.getDeclaredField(elementName),
					fieldCache, c -> new FieldCacheKey(c, elementName),
					f -> f.setAccessible(true));
		}
		
		/* package */ Field getFiltered() throws NoSuchFieldException {
			return ReflectionUtil.getFiltered(clazz, elementName, c -> c.getDeclaredField(elementName),
					fieldCache, c -> new FieldCacheKey(c, elementName),
					f -> f.setAccessible(true), f -> f.getName(),
					"getDeclaredFields0", "copyField");
		}
		
		public Object getValue(Object instance) throws ReflectiveOperationException {
			if (clazz == null && instance != null)
				clazz = instance.getClass();
			return get().get(instance);
		}
		
		public Object getStaticValue() throws ReflectiveOperationException {
			return getValue(null);
		}
		
		public void setValue(Object instance, Object value) throws ReflectiveOperationException {
			if (clazz == null && instance != null)
				clazz = instance.getClass();
			
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
						field("modifiers").inClass(Field.class).getFiltered().set(f, modifiers & ~Modifier.FINAL);
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
	
	public static class MethodReflectionBuilder extends ReflectionBuilder {
		
		Class<?>[] parameterTypes;
		
		/* package */ MethodReflectionBuilder(String name, Class<?>... paramTypes) {
			super(name);
			parameterTypes = paramTypes == null ? new Class<?>[0] : paramTypes;
		}

		// override method so their return type is more specific
		public MethodReflectionBuilder inClass(Class<?> c) { return (MethodReflectionBuilder) super.inClass(c); }
		public MethodReflectionBuilder inClass(String className) throws ClassNotFoundException { return (MethodReflectionBuilder) super.inClass(className); }
		public MethodReflectionBuilder inClassOfInstance(Object inst) { return (MethodReflectionBuilder) super.inClassOfInstance(inst); }
		
		
		public Method get() throws NoSuchMethodException {
			List<Class<?>> parameterTypesList = Arrays.asList(parameterTypes);
			return getDeclaredRecursively(clazz, elementName,
					c -> c.getDeclaredMethod(elementName, parameterTypes),
					methodCache, c -> new MethodCacheKey(c, elementName, parameterTypesList),
					m -> m.setAccessible(true));
		}
		
		/* package */ Method getFiltered() throws NoSuchMethodException {
			List<Class<?>> parameterTypesList = Arrays.asList(parameterTypes);
			return ReflectionUtil.getFiltered(clazz, elementName, c -> c.getDeclaredMethod(elementName, parameterTypes),
					methodCache, c -> new MethodCacheKey(c, elementName, parameterTypesList),
					m -> m.setAccessible(true), m -> m.getName(),
					"getDeclaredMethods0", "copyMethod");
		}
		
		public Object invoke(Object instance, Object... values) throws ReflectiveOperationException {
			if (clazz == null && instance != null)
				clazz = instance.getClass();
			return get().invoke(instance, values);
		}
		
		public Object invokeStatic(Object... values) throws ReflectiveOperationException {
			return invoke(null, values);
		}
	}
	
	
	
	
	
	
	
	
	
	


    private interface GetDeclaredFunction<T, E extends ReflectiveOperationException> {
    	public T get(Class<?> clazz) throws E;
    }
    
    private static <T, E extends ReflectiveOperationException, K> T getDeclaredRecursively(
    		Class<?> clazz, String name, GetDeclaredFunction<T, E> func,
    		Map<K, T> cache, Function<Class<?>, K> cacheKeySupplier, Consumer<T> setAccessible) throws E {
    	Objects.requireNonNull(clazz, "Class instance not provided");
		Objects.requireNonNull(name, "Field name not provided");

    	E ex = null;
    	List<Class<?>> passedClasses = new ArrayList<>();
    	Class<?> currentClass = clazz;
    	T el = null;
    	do {
    		// check cache first
    		el = cache.get(cacheKeySupplier.apply(currentClass));
	    	if (el != null)
	    		break;
	    	
    		passedClasses.add(currentClass);
    		
    		try {
    			el = func.get(currentClass);
    			break; // we found the field
    		} catch (ReflectiveOperationException e) {
    			if (ex == null) {
    				@SuppressWarnings("unchecked")
					E ee = (E) e;
    				ex = ee;
    			}
    		}
    		
    		currentClass = currentClass.getSuperclass();
    		
    	} while(currentClass != null);
    	
    	if (el == null)
    		throw ex;
    	
    	// update cache
    	for (Class<?> c1 : passedClasses) {
    		cache.put(cacheKeySupplier.apply(c1), el);
    	}
    	
    	setAccessible.accept(el);
    	
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
    private static <T, E extends ReflectiveOperationException, K> T getFiltered(
    		Class<?> clazz, String name, GetDeclaredFunction<T, E> func,
    		Map<K, T> cache, Function<Class<?>, K> cacheKeySupplier,
    		Consumer<T> setAccessible, Function<T, String> getName,
    		String privateMethodName, String copyMethodName) throws E {
    	Objects.requireNonNull(clazz, "Class instance not provided");
		Objects.requireNonNull(name, "Field name not provided");

		K cacheKey = cacheKeySupplier.apply(clazz);
		T el = cache.get(cacheKey);
    	if (el != null)
    		return el;
		
    	E ex = null;
		try {
			el = func.get(clazz);
		} catch (ReflectiveOperationException e) {
			@SuppressWarnings("unchecked")
			E ee = (E) e;
			ex = ee;
		}
		
		if (el == null) {
			try {
				@SuppressWarnings("unchecked")
				T[] elements = (T[]) method(privateMethodName, boolean.class).invoke(clazz, false);
				for (T element : elements) {
	        		if (name.equals(getName.apply(element))) {
	        			// the values in the elements array have to be copied
	        			// (using special private methods in reflection api) before using it
	        			Object reflectionFactoryOfClazz = method("getReflectionFactory").invoke(clazz);
	        			@SuppressWarnings("unchecked")
						T copiedElement = (T) method(copyMethodName, element.getClass())
								.inClassOfInstance(reflectionFactoryOfClazz)
								.invoke(reflectionFactoryOfClazz, element);
	        			el = copiedElement;
	                    break;
	                }
		        }
			} catch (ReflectiveOperationException e) {
				if (ex != null)
					ex.addSuppressed(e);
			}
	        
		}
		
		if (el == null)
			throw ex;
		
		setAccessible.accept(el);
		
		cache.put(cacheKey, el);
		
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
