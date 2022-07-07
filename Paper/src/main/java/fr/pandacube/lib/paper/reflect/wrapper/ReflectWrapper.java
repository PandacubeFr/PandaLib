package fr.pandacube.lib.paper.reflect.wrapper;

import com.google.common.collect.MapMaker;
import fr.pandacube.lib.core.util.Reflect;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;

public abstract class ReflectWrapper implements ReflectWrapperI {


	private static final Map<Object, ReflectWrapperI> objectWrapperCache = new MapMaker().weakKeys().makeMap();

	public static Object unwrap(ReflectWrapperI wr) {
		return wr == null ? null : wr.__getRuntimeInstance();
	}

	public static <T> T unwrap(ReflectWrapperTypedI<T> wr) {
		return wr == null ? null : wr.__getRuntimeInstance();
	}

	public static <W extends ReflectWrapperI> W wrap(Object runtimeObj) {
		return wrap(runtimeObj, null);
	}
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
			Reflect.ReflectConstructor<? extends ReflectWrapperI> constructor = WrapperRegistry.getWrapperConstructorOfWrapperClass(wrapperClass);
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

	public static <W extends ReflectWrapperI> ReflectListWrapper<W> wrapList(List<Object> runtimeList, Class<W> expectedWrapperClass) {
		return new ReflectListWrapper<>(runtimeList, expectedWrapperClass);
	}



	
	
	protected final Object reflectObject;
	
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
