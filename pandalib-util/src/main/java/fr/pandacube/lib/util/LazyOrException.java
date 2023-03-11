package fr.pandacube.lib.util;

import java.util.Objects;

import fr.pandacube.lib.util.ThrowableUtil.SupplierException;

/**
 * Represents a lazy loaded value.
 * <p>
 * The value will be computed using the Supplier provided in the
 * constructor, only the first time the {@link #get()} method is
 * called.
 *
 * @param <T> the type of the enclosed value.
 * @param <E> the exception type
 */
public class LazyOrException<T, E extends Exception> implements SupplierException<T, E> {
	
	private T cachedValue;
	private final SupplierException<T, E> supplier;
	private boolean cached = false;

	/**
	 * Create a lazy value loader that will call the provided supplier to get the value.
	 * @param s the supplier from which the value is fetched.
	 */
	public LazyOrException(SupplierException<T, E> s) {
		supplier = Objects.requireNonNull(s);
	}
	
	/**
	 * Get the wrapped value, from cache or from the provider if it is not yet cached.
	 * <p>
	 * If the provider throws an exception, it will be redirected to the caller as is, and no value will be cached
	 * (the next call to this method will execute the supplier again).
	 */
	@Override
	public synchronized T get() throws E {
		if (!cached)
			set(supplier.get());
		return cachedValue;
	}

	/**
	 * Reset the cached value. The next call to {@link #get()} will get the value from the provider.
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

}
