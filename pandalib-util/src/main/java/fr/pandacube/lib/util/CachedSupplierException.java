package fr.pandacube.lib.util;

import fr.pandacube.lib.util.function.SupplierException;

import java.util.Objects;

/**
 * A Supplier that cache the value the first time it is called.
 *
 * @param <T> the type of the supplied value.
 * @param <E> the exception type that may be thrown by the source supplier.
 */
public class CachedSupplierException<T, E extends Exception> implements SupplierException<T, E> {
	
	private T cachedValue;
	private final SupplierException<T, E> source;
	private boolean cached = false;

	/**
	 * Create a lazy value loader that will call the provided supplier to get the value.
	 * @param s the supplier from which the value is fetched.
	 */
	public CachedSupplierException(SupplierException<T, E> s) {
		source = Objects.requireNonNull(s);
	}
	
	/**
	 * Get the value, from cache or from the provider if it's not yet cached.
	 * <p>
	 * If the provider throws an exception, it will be redirected to the caller as is, and no value will be cached
	 * (the next call to this method will execute the supplier again).
	 */
	@Override
	public synchronized T get() throws E {
		if (!cached)
			set(source.get());
		return cachedValue;
	}

	/**
	 * Reset the cached value. The next call to {@link #get()} will get the value from the source.
	 */
	public synchronized void reset() {
		cached = false;
		cachedValue = null;
	}

	/**
	 * Manually set the value to the provided one.
	 * @param value the value to put in the cache.
	 */
	public synchronized void set(T value) {
		cachedValue = value;
		cached = true;
	}

	/**
	 * Tells if the value is currently set or not.
	 * @return true if the value has been set by calling the method {@link #get()} or {@link #set(Object)} but not yet
	 * reset by {@link #reset()}, or false otherwise.
	 */
	public synchronized boolean isSet() {
		return cached;
	}

}
