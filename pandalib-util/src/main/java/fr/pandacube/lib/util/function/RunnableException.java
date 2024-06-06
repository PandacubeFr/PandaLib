package fr.pandacube.lib.util.function;

/**
 * A runnable that can possibly throw a checked exception.
 * @param <E> the exception type that this interface method can throw.
 */
@FunctionalInterface
public interface RunnableException<E extends Exception> {
    /**
     * Run any code implemented.
     *
     * @throws E if implementation failed to run.
     */
    void run() throws E;
}
