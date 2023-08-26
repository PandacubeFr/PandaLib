package fr.pandacube.lib.util;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Represents a lazy loaded value.
 * 
 * The value will be computed using the Supplier provided in the
 * constructor, only the first time the {@link #get()} method is
 * called.
 *
 * @param <T> the type of the enclosed value.
 */
public class Lazy<T> implements Supplier<T> {
	
	private T cachedValue;
	private final Supplier<T> supplier;
	private boolean cached = false;

	/**
	 * Create a lazy value loader that will call the provided supplier to get the value.
	 * @param s the supplier from which the value is fetched.
	 */
	public Lazy(Supplier<T> s) {
		supplier = Objects.requireNonNull(s);
	}
	
	/**
	 * Get the wrapped value, from cache or from the provider if it is not yet cached.
	 * 
	 * If the provider throws an exception, it will be redirected to the caller as is, and no value will be cached
	 * (the next call to this method will execute the supplier again).
	 */
	@Override
	public synchronized T get() {
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

	/**
	 * Tells if the value is currently set or not.
	 * @return true if the value has been set by calling the method {@link #get()} or {@link #set(Object)} but not yet
	 * reset by {@link #reset()}, or false otherwise.
	 */
	public synchronized boolean isSet() {
		return cached;
	}

}
