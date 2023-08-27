package fr.pandacube.lib.util.function;

/**
 * A consumer that can possibly throw a checked exception.
 */
@FunctionalInterface
public interface BiConsumerException<T, U, E extends Exception> {
    /**
     * Run the consumer on the specified parameters.
     *
     * @param t the first parameter of the consumer.
     * @param u the second parameter of the consumer.
     * @throws E if the function fails.
     */
    void accept(T t, U u) throws E;
}
