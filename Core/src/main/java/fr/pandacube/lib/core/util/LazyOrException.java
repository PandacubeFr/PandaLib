package fr.pandacube.lib.core.util;

import java.util.Objects;

/**
 * Represents a lazy loaded value.
 * 
 * The value will be computed using the Supplier provided in the
 * constructor, only the first time the {@link #get()} method is
 * called.
 *
 * @param <T> the type of the enclosed value.
 */
public class LazyOrException<T> {
	
	private T cachedValue;
	private final SupplierException<T> supplier;
	private boolean cached = false;
	
	public LazyOrException(SupplierException<T> s) {
		supplier = Objects.requireNonNull(s);
	}
	
	/**
	 * Get the wrapped value, from cache or from the provider if it is not yet cached.
	 * 
	 * If the provider throws an exception, it will be redirected to the caller as is, and no value will be cached (the next call to this method will
	 * execute the supplier again).
	 */
	public synchronized T get() throws Exception {
		if (!cached) // check outside synchronized method to reduce useless synchronization if value is already cached
			set(supplier.get());
		return cachedValue;
	}
	
	public synchronized void reset() {
		cached = false;
		cachedValue = null;
	}
	
	public synchronized void set(T value) {
		cachedValue = value;
		cached = true;
	}
	
	
	@FunctionalInterface
	public interface SupplierException<T> {
		public T get() throws Exception;
	}

}
