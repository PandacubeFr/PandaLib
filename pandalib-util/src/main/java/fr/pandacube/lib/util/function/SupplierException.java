package fr.pandacube.lib.util.function;

/**
 * A supplier that can possibly throw a checked exception.
 * @param <T> the return type of this supplier.
 * @param <E> the exception type that this interface method can throw.
 */
@FunctionalInterface
public interface SupplierException<T, E extends Exception> {
    /**
     * Gets a result.
     *
     * @return a result.
     * @throws E if implementation failed to run.
     */
    T get() throws E;
}
