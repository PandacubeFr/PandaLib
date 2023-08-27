package fr.pandacube.lib.util.function;

/**
 * A runnable that can possibly throw a checked exception.
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
