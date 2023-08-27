package fr.pandacube.lib.util;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A Supplier that cache the value the first time it is called.
 *
 * @param <T> the type of the supplied value.
 */
public class CachedSupplier<T> implements Supplier<T> {
	
	private T cachedValue;
	private final Supplier<T> source;
	private boolean cached = false;

	/**
	 * Create a lazy value loader that will call the provided supplier to get the value.
	 * @param s the supplier from which the value is fetched.
	 */
	public CachedSupplier(Supplier<T> s) {
		source = Objects.requireNonNull(s);
	}
	
	/**
	 * Get the value, from cache or from the source supplier if it's not yet cached.
	 * <p>
	 * If the provider throws an exception, it will be redirected to the caller as is, and no value will be cached
	 * (the next call to this method will execute the supplier again).
	 */
	@Override
	public synchronized T get() {
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
