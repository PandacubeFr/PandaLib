package fr.pandacube.lib.util.function;

/**
 * A consumer that can possibly throw a checked exception.
 * @param <T> the first parameter for this tri-consumer.
 * @param <U> the second parameter for this tri-consumer.
 * @param <V> the third parameter for this tri-consumer.
 * @param <E> the exception type that this interface method can throw.
 */
@FunctionalInterface
public interface TriConsumerException<T, U, V, E extends Exception> {
    /**
     * Run the consumer on the specified parameters.
     *
     * @param t the first parameter of the consumer.
     * @param u the second parameter of the consumer.
     * @param v the third parameter of the consumer.
     * @throws E if the function fails.
     */
    void accept(T t, U u, V v) throws E;
}
