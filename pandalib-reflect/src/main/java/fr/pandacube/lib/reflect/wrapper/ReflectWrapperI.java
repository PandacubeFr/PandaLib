package fr.pandacube.lib.reflect.wrapper;

/**
 * Interface implemented by all reflect wrapper objects.
 */
public interface ReflectWrapperI {

	/**
	 * Gets the class of the wrapped object.
	 * @return the class of the wrapped object.
	 */
	default Class<?> __getRuntimeClass() {
		return WrapperRegistry.getRuntimeClassOfWrapperClass(getClass());
	}

	/**
	 * Returns the wrapped object.
	 * @return the wrapped object.
	 */
	Object __getRuntimeInstance();
	

}
