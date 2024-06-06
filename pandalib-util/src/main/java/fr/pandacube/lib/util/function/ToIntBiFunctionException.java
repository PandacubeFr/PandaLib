package fr.pandacube.lib.util.function;

/**
 * A function that can possibly throw a checked exception.
 * @param <T> the first parameter for this bi-function.
 * @param <U> the second parameter for this bi-function.
 * @param <E> the exception type that this interface method can throw.
 */
@FunctionalInterface
public interface ToIntBiFunctionException<T, U, E extends Exception> {
    /**
     * Run on the specified parameters to return an int value.
     *
     * @param t the first parameter of the function.
     * @param u the second parameter of the function.
     * @return the result of the function.
     * @throws E if the function fails.
     */
    int applyAsInt(T t, U u) throws E;
}
