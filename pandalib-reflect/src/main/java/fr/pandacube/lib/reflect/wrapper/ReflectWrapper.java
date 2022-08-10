package fr.pandacube.lib.reflect.wrapper;

import com.google.common.collect.MapMaker;

import fr.pandacube.lib.reflect.ReflectConstructor;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

/**
 * Superclass of all reflect wrapper objects.
 */
public abstract class ReflectWrapper implements ReflectWrapperI {


	private static final Map<Object, ReflectWrapperI> objectWrapperCache = new MapMaker().weakKeys().makeMap();

	/**
	 * Unwraps the object from the provided reflect wrapper.
	 * @param wr the reflect wrapper from which to get the object.
	 * @return the object from the provided reflect wrapper.
	 */
	public static Object unwrap(ReflectWrapperI wr) {
		return wr == null ? null : wr.__getRuntimeInstance();
	}

	/**
	 * Unwraps the object from the provided reflect wrapper.
	 * @param wr the reflect wrapper from which to get the object.
	 * @param <T> the type of the wrapped object.
	 * @return the object from the provided reflect wrapper.
	 */
	public static <T> T unwrap(ReflectWrapperTypedI<T> wr) {
		return wr == null ? null : wr.__getRuntimeInstance();
	}

	/**
	 * Wraps the provided runtime object into a reflect wrapper.
	 * If a wrapper instance is already known, it will return it instead of instanciating a new one.
	 * It is better to call {@link #wrap(Object, Class)} if you know the type of wrapper needed.
	 * @param runtimeObj the object to wrap.
	 * @return the reflect wrapper wrapping the provided object.
	 * @throws ClassCastException if the runtime class of the object is not handled by the expected wrapper class or its
	 *                            subclasses.
	 * @throws IllegalArgumentException if the runtime class of the object is not handled by any of the registered
	 *                                  wrapper classes.
	 */
	public static ReflectWrapperI wrap(Object runtimeObj) {
		return wrap(runtimeObj, null);
	}

	/**
	 * Wraps the provided runtime object (with has a known type) into a reflect wrapper.
	 * If a wrapper instance is already known, it will return it instead of instanciating a new one.
	 * It is better to call {@link #wrap(Object, Class)} if you know the type of wrapper needed.
	 * @param runtimeObj the object to wrap.
	 * @param expectedWrapperClass the reflect wrapper class expected to be returned.
	 * @param <W> the type of the reflect wrapper.
	 * @param <T> the type of the wrapped object.
	 * @return the reflect wrapper wrapping the provided object.
	 * @throws ClassCastException if the runtime class of the object is not handled by the expected wrapper class or its
	 *                            subclasses.
	 * @throws IllegalArgumentException if the runtime class of the object is not handled by any of the registered
	 *                                  wrapper classes.
	 */
	public static <T, W extends ReflectWrapperTypedI<T>> W wrapTyped(T runtimeObj, Class<W> expectedWrapperClass) {
		return wrap(runtimeObj, expectedWrapperClass);
	}

	/**
	 * Wraps the provided runtime object into a reflect wrapper.
	 * If a wrapper instance is already known, it will return it instead of instanciating a new one.
	 * It is better to call {@link #wrap(Object, Class)} if you know the type of wrapper needed.
	 * @param runtimeObj the object to wrap.
	 * @param expectedWrapperClass the reflect wrapper class expected to be returned.
	 * @param <W> the type of the reflect wrapper.
	 * @return the reflect wrapper wrapping the provided object.
	 * @throws ClassCastException if the runtime class of the object is not handled by the expected wrapper class or its
	 *                            subclasses.
	 * @throws IllegalArgumentException if the runtime class of the object is not handled by any of the registered
	 *                                  wrapper classes.
	 */
	public static <W extends ReflectWrapperI> W wrap(Object runtimeObj, Class<W> expectedWrapperClass) {
		if (runtimeObj == null)
			return null;
		synchronized (objectWrapperCache) {
			if (objectWrapperCache.containsKey(runtimeObj)) {
				ReflectWrapperI wrapper = objectWrapperCache.get(runtimeObj);
				if (expectedWrapperClass == null || expectedWrapperClass.isInstance(wrapper)) {
					@SuppressWarnings("unchecked")
					W wr = (W) wrapper;
					return wr;
				}
			}
			Class<?> runtimeClass = runtimeObj.getClass();
			Class<?> expectedRuntimeClass = (expectedWrapperClass == null) ? null : WrapperRegistry.getRuntimeClassOfWrapperClass(expectedWrapperClass);
			if (expectedRuntimeClass != null && !expectedRuntimeClass.isAssignableFrom(runtimeClass)) {
				throw new ClassCastException("Runtime class " + runtimeClass + " is not a sub-class or a sub-interface of expected runtime class " + expectedRuntimeClass + "" +
						" (expected wrapper class " + expectedWrapperClass + ").");
			}
			Class<? extends ReflectWrapperI> wrapperClass = WrapperRegistry.getWrapperOfRuntimeClass(runtimeClass);
			if (wrapperClass == null) {
				// trying to use the provided expectedWrapperClass
				if (expectedWrapperClass == null || expectedRuntimeClass == null) { // implicitly: expectedWrapperClass is null or it has no corresponding runtimeClass
					// TODO try to search among all registered wrapper class for one that can support the provided object
					throw new IllegalArgumentException("No wrapper available to wrap an instance of runtime class " + runtimeClass + "." +
							(expectedWrapperClass != null ? (" Expected wrapper class " + expectedWrapperClass + " is also not valid.") : ""));
				}
				wrapperClass = expectedWrapperClass;
			}

			if (expectedWrapperClass != null && !expectedWrapperClass.isAssignableFrom(wrapperClass)) {
				throw new ClassCastException("Wrapper class " + wrapperClass + " is not a sub-class or a sub-interface of expected wrapper class" + expectedWrapperClass);
			}
			ReflectConstructor<? extends ReflectWrapperI> constructor = WrapperRegistry.getWrapperConstructorOfWrapperClass(wrapperClass);
			if (constructor == null) {
				throw new IllegalStateException("Unable to find a constructor to instanciate " + wrapperClass + " to wrap an instance of " + runtimeObj);
			}
			ReflectWrapperI wrapper = wrapEx(() -> constructor.instanciate(runtimeObj));
			// added to cache by constructor
			@SuppressWarnings("unchecked")
			W wr = (W) wrapper;
			return wr;
		}
	}

	/**
	 * Wraps the provided runtime list into a reflect list wrapper.
	 * @param runtimeList the list of runtime object to wrap.
	 * @param expectedWrapperClass the wrapper class of the objects in this list.
	 * @param <W> the type of reflect wrapper for the objects in this list.
	 * @return a reflect list wrapper wrapping the provided list.
	 */
	public static <W extends ReflectWrapperI> ReflectListWrapper<W> wrapList(List<Object> runtimeList, Class<W> expectedWrapperClass) {
		return new ReflectListWrapper<>(runtimeList, expectedWrapperClass);
	}












	private final Object reflectObject;

	/**
	 * Instanciate this Reflect Wrapper with the provided object.
	 * Any subclasses should not make their constructor public since the instanciation is managed by {@link #wrap(Object, Class) wrap(...)}.
	 * @param obj the object to wrap. It must be an instance of the {@link #__getRuntimeClass() runtime class} of this
	 *            wrapper class.
	 */
	protected ReflectWrapper(Object obj) {
		Objects.requireNonNull(obj);
		if (!__getRuntimeClass().isInstance(obj)) {
			throw new ClassCastException(obj.getClass() + " object is not instanceof " + __getRuntimeClass());
		}
		reflectObject = obj;
		objectWrapperCache.put(obj, this);
	}

	@Override
	public Object __getRuntimeInstance() {
		return reflectObject;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ReflectWrapper wr) {
			return Objects.equals(reflectObject, wr.reflectObject);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(reflectObject);
	}



}
