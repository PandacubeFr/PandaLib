package fr.pandacube.lib.util.function;

/**
 * A predicate that can possibly throw a checked exception.
 */
@FunctionalInterface
public interface PredicateException<T, E extends Exception> {
    /**
     * Test the predicate on the specified value.
     *
     * @param value the value to test against.
     * @return the result of the test.
     * @throws E if implementation failed to run.
     */
    boolean test(T value) throws E;
}
