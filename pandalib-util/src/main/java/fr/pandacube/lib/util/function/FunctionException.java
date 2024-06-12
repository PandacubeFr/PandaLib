package fr.pandacube.lib.util.function;

/**
 * A function that can possibly throw a checked exception.
 * @param <T> the parameter for this function.
 * @param <R> the return value for this function.
 * @param <E> the exception type that this interface method can throw.
 */
@FunctionalInterface
public interface FunctionException<T, R, E extends Exception> {
    /**
     * Run on the specified parameters to return an int value.
     *
     * @param t the parameter of the function.
     * @return the result of the function.
     * @throws E if the function fails.
     */
    R apply(T t) throws E;
}
