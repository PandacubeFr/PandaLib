package fr.pandacube.lib.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

public class ReflexionUtil {

	public static Object getDeclaredFieldValue(Object instance, String fieldName) throws ReflectiveOperationException {
		if (instance == null)
			throw new IllegalArgumentException("instance can't be null");
		return getDeclaredFieldValue(instance.getClass(), instance, fieldName);
	}
	
	public static Object getDeclaredFieldValue(String className, String fieldName) throws ReflectiveOperationException {
		return getDeclaredFieldValue(Class.forName(className), null, fieldName);
	}
	
	public static Object getDeclaredFieldValue(String className, Object instance, String fieldName) throws ReflectiveOperationException {
		return getDeclaredFieldValue(Class.forName(className), instance, fieldName);
	}
	
	public static Object getDeclaredFieldValue(Class<?> clazz, String fieldName) throws ReflectiveOperationException {
		return getDeclaredFieldValue(clazz, null, fieldName);
	}
	
	public static Object getDeclaredFieldValue(Class<?> clazz, Object instance, String fieldName) throws ReflectiveOperationException {
		Field f = clazz.getDeclaredField(fieldName);
		f.setAccessible(true);
		return f.get(instance);
	}

	
	public static Object getFieldValue(Object instance, String fieldName) throws ReflectiveOperationException {
		if (instance == null)
			throw new IllegalArgumentException("instance can't be null");
		return getFieldValue(instance.getClass(), instance, fieldName);
	}
	
	public static Object getFieldValue(String className, String fieldName) throws ReflectiveOperationException {
		return getFieldValue(Class.forName(className), null, fieldName);
	}
	
	public static Object getFieldValue(String className, Object instance, String fieldName) throws ReflectiveOperationException {
		return getFieldValue(Class.forName(className), instance, fieldName);
	}
	
	public static Object getFieldValue(Class<?> clazz, String fieldName) throws ReflectiveOperationException {
		return getFieldValue(clazz, null, fieldName);
	}
	
	public static Object getFieldValue(Class<?> clazz, Object instance, String fieldName) throws ReflectiveOperationException {
		Field f = clazz.getField(fieldName);
		f.setAccessible(true);
		return f.get(instance);
	}

	
	public static Object invokeMethod(Object instance, String methodName) throws ReflectiveOperationException {
		if (instance == null)
			throw new IllegalArgumentException("instance can't be null");
		return invokeMethod(instance, methodName, new Class<?>[0]);
	}
	
	public static Object invokeMethod(Object instance, String methodName, Class<?>[] parameterTypes, Object... args) throws ReflectiveOperationException {
		if (instance == null)
			throw new IllegalArgumentException("instance can't be null");
		return invokeMethod(instance.getClass(), instance, methodName, parameterTypes, args);
	}
	
	public static Object invokeMethod(String className, String methodName) throws ReflectiveOperationException {
		return invokeMethod(Class.forName(className), null, methodName, new Class<?>[0]);
	}
	
	public static Object invokeMethod(String className, String methodName, Class<?>[] parameterTypes, Object... args) throws ReflectiveOperationException {
		return invokeMethod(Class.forName(className), null, methodName, parameterTypes, args);
	}
	
	public static Object invokeMethod(String className, Object instance, String methodName) throws ReflectiveOperationException {
		return invokeMethod(Class.forName(className), instance, methodName, new Class<?>[0]);
	}
	
	public static Object invokeMethod(String className, Object instance, String methodName, Class<?>[] parameterTypes, Object... args) throws ReflectiveOperationException {
		return invokeMethod(Class.forName(className), instance, methodName, parameterTypes, args);
	}
	
	public static Object invokeMethod(Class<?> clazz, String methodName) throws ReflectiveOperationException {
		return invokeMethod(clazz, null, methodName, new Class<?>[0]);
	}
	
	public static Object invokeMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes, Object... args) throws ReflectiveOperationException {
		return invokeMethod(clazz, null, methodName, parameterTypes, args);
	}
	
	public static Object invokeMethod(Class<?> clazz, Object instance, String methodName) throws ReflectiveOperationException {
		return invokeMethod(clazz, instance, methodName, new Class<?>[0]);
	}
	
	public static Object invokeMethod(Class<?> clazz, Object instance, String methodName, Class<?>[] parameterTypes, Object... args) throws ReflectiveOperationException {
		Method m = clazz.getMethod(methodName, parameterTypes);
		m.setAccessible(true);
		return m.invoke(instance, args);
	}


	public static Object invokeDeclaredMethod(Object instance, String methodName) throws ReflectiveOperationException {
		if (instance == null)
			throw new IllegalArgumentException("instance can't be null");
		return invokeDeclaredMethod(instance, methodName, new Class<?>[0]);
	}
	
	public static Object invokeDeclaredMethod(Object instance, String methodName, Class<?>[] parameterTypes, Object... args) throws ReflectiveOperationException {
		if (instance == null)
			throw new IllegalArgumentException("instance can't be null");
		return invokeDeclaredMethod(instance.getClass(), instance, methodName, parameterTypes, args);
	}
	
	public static Object invokeDeclaredMethod(String className, String methodName) throws ReflectiveOperationException {
		return invokeDeclaredMethod(Class.forName(className), null, methodName, new Class<?>[0]);
	}
	
	public static Object invokeDeclaredMethod(String className, String methodName, Class<?>[] parameterTypes, Object... args) throws ReflectiveOperationException {
		return invokeDeclaredMethod(Class.forName(className), null, methodName, parameterTypes, args);
	}
	
	public static Object invokeDeclaredMethod(String className, Object instance, String methodName) throws ReflectiveOperationException {
		return invokeDeclaredMethod(Class.forName(className), instance, methodName, new Class<?>[0]);
	}
	
	public static Object invokeDeclaredMethod(String className, Object instance, String methodName, Class<?>[] parameterTypes, Object... args) throws ReflectiveOperationException {
		return invokeDeclaredMethod(Class.forName(className), instance, methodName, parameterTypes, args);
	}
	
	public static Object invokeDeclaredMethod(Class<?> clazz, String methodName) throws ReflectiveOperationException {
		return invokeDeclaredMethod(clazz, null, methodName, new Class<?>[0]);
	}
	
	public static Object invokeDeclaredMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes, Object... args) throws ReflectiveOperationException {
		return invokeDeclaredMethod(clazz, null, methodName, parameterTypes, args);
	}
	
	public static Object invokeDeclaredMethod(Class<?> clazz, Object instance, String methodName) throws ReflectiveOperationException {
		return invokeDeclaredMethod(clazz, instance, methodName, new Class<?>[0]);
	}
	
	public static Object invokeDeclaredMethod(Class<?> clazz, Object instance, String methodName, Class<?>[] parameterTypes, Object... args) throws ReflectiveOperationException {
		Method m = clazz.getDeclaredMethod(methodName, parameterTypes);
		m.setAccessible(true);
		return m.invoke(instance, args);
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
