package fr.pandacube.lib.util.function;

/**
 * A predicate that can possibly throw a checked exception.
 * @param <T> the parameter for this predicate.
 * @param <E> the exception type that this predicate can throw.
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
