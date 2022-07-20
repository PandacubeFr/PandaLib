package fr.pandacube.lib.reflect;

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
import java.util.concurrent.atomic.AtomicReference;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassGraphException;
import io.github.classgraph.ClassInfoList;
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
			return other instanceof MethodIdentifier o
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
			return other instanceof ConstructorIdentifier o
					&& Arrays.equals(o.parameters, parameters);
		}
    	@Override
    	public int hashCode() {
    		return Arrays.hashCode(parameters);
    	}
    }
    
    
    
    
    
    
    public static class ReflectClass<T> {
    	private final Class<T> clazz;

        private final Map<MethodIdentifier, ReflectMethod<T>> methodCache = Collections.synchronizedMap(new HashMap<>());
        private final Map<ConstructorIdentifier, ReflectConstructor<T>> constructorCache = Collections.synchronizedMap(new HashMap<>());
        private final Map<String, ReflectField<T>> fieldCache = Collections.synchronizedMap(new HashMap<>());
    	
    	private ReflectClass(Class<T> clazz) {
    		this.clazz = clazz;
		}
    	
    	/**
    	 * Returns the class wrapped by this ReflectClass instance.
    	 */
    	public Class<T> get() {
    		return clazz;
    	}
    	
    	private ReflectMethod<T> method(MethodIdentifier key, boolean bypassFilter) throws NoSuchMethodException {
			ReflectMethod<T> method = methodCache.get(key);
    		if (method == null) {
    			method = new ReflectMethod<>(this, key, bypassFilter);
    			methodCache.put(key, method);
    		}
    		return method;
    	}
    	
    	public ReflectMethod<T> method(String name, Class<?>... paramTypes) throws NoSuchMethodException {
    		return method(new MethodIdentifier(name, paramTypes), false);
    	}
    	
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
    	
    	public ReflectConstructor<T> constructor(Class<?>... paramTypes) throws NoSuchMethodException {
    		return constructor(new ConstructorIdentifier(paramTypes), false);
    	}
    	
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
    	
    	public ReflectField<T> field(String name) throws NoSuchFieldException {
    		return field0(name, false);
    	}
    	
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
		 * @throws ClassGraphException if any of the worker threads throws an uncaught exception, or the scan was interrupted. (see {@link ClassGraph#scan})
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
    
    
    
    
    
    
    
    
    
    

    
    
    
    
    
    
	
	
	
	
	
	
	public static abstract class ReflectMember<T, ID, EL, EX extends ReflectiveOperationException> {
		protected final ReflectClass<T> reflectClass;
		protected final ID identifier;
		
		protected final EL cached;
		
		protected ReflectMember(ReflectClass<T> c, ID id, boolean bypassFilter) throws EX {
			reflectClass = c;
			identifier = id;
			cached = (bypassFilter) ? fetchFiltered() : fetch();
		}
		
		
		
		protected EL fetch() throws EX {
			
			// get element in current class
			try {
				EL el = fetchFromClass(reflectClass.clazz);
				setAccessible(el);
				return el;
			} catch (ReflectiveOperationException e1) {
				@SuppressWarnings("unchecked")
				EX ex = (EX) e1;

				// get parent class
				Class<? super T> superClass = reflectClass.clazz.getSuperclass();
				if (superClass == null)
					throw ex;

				// get element in parent class (will do recursion)
				try {
					EL el = fetchFromReflectClass(ofClass(superClass));
					setAccessible(el);
					return el;
				} catch (ReflectiveOperationException e2) {
					ex.addSuppressed(e2);
					throw ex;
				}
			}
		}
		
		protected EL fetchFiltered() throws EX {

			// get element in current class
			try {
				EL el = fetchFromClass(reflectClass.clazz);
				setAccessible(el);
				return el;
			} catch (ReflectiveOperationException e1) {
				@SuppressWarnings("unchecked")
				EX ex = (EX) e1;

				// trying to bypass filtered member
				try {
					@SuppressWarnings("unchecked")
					EL[] elements = (EL[]) Reflect.ofClassOfInstance(reflectClass.clazz)
							.method(internalMethodNameElementArray(), boolean.class)
							.invoke(reflectClass.clazz, false);
					for (EL element : elements) {
						if (isEqualOurElement(element)) {
							// the values in the elements array have to be copied
							// (using special private methods in reflection api) before using it
							Object reflectionFactoryOfClazz = Reflect.ofClassOfInstance(reflectClass.clazz)
									.method("getReflectionFactory")
									.invoke(reflectClass.clazz);
							@SuppressWarnings("unchecked")
							EL copiedElement = (EL) Reflect.ofClassOfInstance(reflectionFactoryOfClazz)
									.method(internalMethodNameCopyElement(), element.getClass())
									.invoke(reflectionFactoryOfClazz, element);
							setAccessible(copiedElement);
							return copiedElement;
						}
					}
				} catch (ReflectiveOperationException e2) {
					ex.addSuppressed(e2);
				}

				throw ex;
			}
		}
		
		protected abstract EL fetchFromClass(Class<T> clazz) throws EX;
		protected abstract EL fetchFromReflectClass(ReflectClass<?> rc) throws EX;
		protected abstract boolean isEqualOurElement(EL el);
		protected abstract String internalMethodNameElementArray();
		protected abstract String internalMethodNameCopyElement();
		protected abstract void setAccessible(EL el);
		
		public EL get() {
			return cached;
		}
		
		public abstract int getModifiers();
		
	}
	
	
	
	
	
	
	
	public static class ReflectField<T> extends ReflectMember<T, String, Field, NoSuchFieldException> {
		
		/* package */ ReflectField(ReflectClass<T> c, String name, boolean bypassFilter) throws NoSuchFieldException {
			super(c, name, bypassFilter);
		}

		@Override protected Field fetchFromClass(Class<T> clazz) throws NoSuchFieldException { return clazz.getDeclaredField(identifier); }
		@Override protected Field fetchFromReflectClass(ReflectClass<?> rc) throws NoSuchFieldException { return rc.field(identifier).get(); }
		@Override protected boolean isEqualOurElement(Field el) { return identifier.equals(el.getName()); }
		@Override protected String internalMethodNameElementArray() { return "getDeclaredFields0"; }
		@Override protected String internalMethodNameCopyElement() { return "copyField"; }
		@Override protected void setAccessible(Field el) { el.setAccessible(true); }
		
		
		
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
						ofClass(Field.class).field("modifiers").fetchFiltered().set(f, modifiers & ~Modifier.FINAL);
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
		
		@Override
		public int getModifiers() {
			return get().getModifiers();
		}
		
	}
	
	
	
	
	
	
	
	public static class ReflectMethod<T> extends ReflectMember<T, MethodIdentifier, Method, NoSuchMethodException> {
		
		/* package */ ReflectMethod(ReflectClass<T> c, MethodIdentifier methodId, boolean bypassFilter) throws NoSuchMethodException {
			super(c, methodId, bypassFilter);
		}

		@Override protected Method fetchFromClass(Class<T> clazz) throws NoSuchMethodException { return clazz.getDeclaredMethod(identifier.methodName, identifier.parameters); }
		@Override protected Method fetchFromReflectClass(ReflectClass<?> rc) throws NoSuchMethodException { return rc.method(identifier, false).get(); }
		@Override protected boolean isEqualOurElement(Method el) { return identifier.methodName.equals(el.getName()) && Arrays.equals(identifier.parameters, el.getParameterTypes()); }
		@Override protected String internalMethodNameElementArray() { return "getDeclaredMethods0"; }
		@Override protected String internalMethodNameCopyElement() { return "copyMethod"; }
		@Override protected void setAccessible(Method el) { el.setAccessible(true); }
		
		public Object invoke(Object instance, Object... values) throws ReflectiveOperationException {
			return get().invoke(instance, values);
		}
		
		public Object invokeStatic(Object... values) throws ReflectiveOperationException {
			return invoke(null, values);
		}
		
		@Override
		public int getModifiers() {
			return get().getModifiers();
		}
		
	}
	
	
	
	
	
	
	public static class ReflectConstructor<T> extends ReflectMember<T, ConstructorIdentifier, Constructor<T>, NoSuchMethodException> {
		
		/* package */ ReflectConstructor(ReflectClass<T> c, ConstructorIdentifier constructorId, boolean bypassFilter) throws NoSuchMethodException {
			super(c, constructorId, bypassFilter);
		}

		// Override since we don't want to recursively search for a constructor
		@Override
		protected Constructor<T> fetch() throws NoSuchMethodException {
			Constructor<T> el = fetchFromClass(reflectClass.clazz);
			setAccessible(el);
			return el;
		}

		@Override protected Constructor<T> fetchFromClass(Class<T> clazz) throws NoSuchMethodException { return clazz.getDeclaredConstructor(identifier.parameters); }
		@Override protected Constructor<T> fetchFromReflectClass(ReflectClass<?> rc) { throw new UnsupportedOperationException(); }
		@Override protected boolean isEqualOurElement(Constructor<T> el) { return Arrays.equals(identifier.parameters, el.getParameterTypes()); }
		@Override protected String internalMethodNameElementArray() { return "getDeclaredConstructors0"; }
		@Override protected String internalMethodNameCopyElement() { return "copyConstructor"; }
		@Override protected void setAccessible(Constructor<T> el) { el.setAccessible(true); }
		
		public T instanciate(Object... values) throws ReflectiveOperationException {
			return get().newInstance(values);
		}
		
		@Override
		public int getModifiers() {
			return get().getModifiers();
		}
		
	}
	

	

}
