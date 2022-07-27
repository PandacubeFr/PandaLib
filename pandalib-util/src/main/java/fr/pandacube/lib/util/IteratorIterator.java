package fr.pandacube.lib.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An {@link Iterator} that iterate over all the elements of all the provided iterators.
 * In other words, this class concatenate the provided iterators.
 * @param <T> the type of the values in the iterators
 */
public class IteratorIterator<T> implements Iterator<T> {

	/**
	 * Create an {@link IteratorIterator} with the provided {@link Collection} of {@link Iterable}.
	 * The iterables’ iterators will be concatenated in the order of the collection’s iterator.
	 * @param coll the collection of iterables.
	 * @return a new instance of {@link IteratorIterator} iterating over the elements of the provided iterables.
	 * @param <T> the type of the values in the iterables.
	 */
	public static <T> IteratorIterator<T> ofCollectionOfIterable(Collection<Iterable<T>> coll) {
		return new IteratorIterator<>(coll.stream().map(Iterable::iterator).iterator());
	}

	/**
	 * Create an {@link IteratorIterator} with the provided {@link Collection} of {@link Iterator}.
	 * The iterators will be concatenated in the order of the collection’s iterator.
	 * @param coll the collection of iterators.
	 * @return a new instance of {@link IteratorIterator} iterating over the elements of the provided iterators.
	 * @param <T> the type of the values in the iterators.
	 */
	public static <T> IteratorIterator<T> ofCollectionOfIterator(Collection<Iterator<T>> coll) {
		return new IteratorIterator<>(new ArrayList<>(coll).iterator());
	}

	/**
	 * Create an {@link IteratorIterator} with the provided array of {@link Iterable}.
	 * The iterables’ iterators will be concatenated in the order of the array.
	 * @param arr the array of iterables.
	 * @return a new instance of {@link IteratorIterator} iterating over the elements of the provided iterables.
	 * @param <T> the type of the values in the iterables.
	 */
	@SafeVarargs
	public static <T> IteratorIterator<T> ofArrayOfIterable(Iterable<T>... arr) {
		return new IteratorIterator<>(Arrays.stream(arr).map(Iterable::iterator).iterator());
	}

	/**
	 * Create an {@link IteratorIterator} with the provided array of {@link Iterator}.
	 * The iterators will be concatenated in the order of the array.
	 * @param arr the array of iterators.
	 * @return a new instance of {@link IteratorIterator} iterating over the elements of the provided iterators.
	 * @param <T> the type of the values in the iterators.
	 */
	@SafeVarargs
	public static <T> IteratorIterator<T> ofArrayOfIterator(Iterator<T>... arr) {
		return new IteratorIterator<>(Arrays.asList(arr).iterator());
	}





	private final Iterator<Iterator<T>> iterators;

	private Iterator<T> currentValueIterator = null;
	private Iterator<T> nextValueIterator = null;
	
	private IteratorIterator(Iterator<Iterator<T>> iterators) {
		this.iterators = iterators;
	}
	
	private void fixNextIterator() {
		if (nextValueIterator != null && !nextValueIterator.hasNext()) {
			nextValueIterator = null;
		}
	}
	private void fixState() {
		fixNextIterator();
		while (nextValueIterator == null && iterators.hasNext()) {
			nextValueIterator = iterators.next();
			fixNextIterator();
		}
	}

	@Override
	public boolean hasNext() {
		fixState();
		return nextValueIterator != null && nextValueIterator.hasNext();
	}
	
	@Override
	public T next() {
		if (!hasNext())
			throw new NoSuchElementException("No next value found in iterator.");
		currentValueIterator = nextValueIterator;
		return currentValueIterator.next();
	}

	@Override
	public void remove() {
		if (currentValueIterator == null)
			throw new IllegalStateException();
		currentValueIterator.remove();
	}
}
