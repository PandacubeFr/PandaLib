package fr.pandacube.lib.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IteratorIterator<T> implements Iterator<T> {

	public static <T> IteratorIterator<T> ofCollectionOfIterable(Collection<Iterable<T>> coll) {
		return new IteratorIterator<>(coll.stream().map(Iterable::iterator).iterator());
	}
	
	public static <T> IteratorIterator<T> ofCollectionOfIterator(Collection<Iterator<T>> coll) {
		return new IteratorIterator<>(new ArrayList<>(coll).iterator());
	}

	@SafeVarargs
	public static <T> IteratorIterator<T> ofArrayOfIterable(Iterable<T>... arr) {
		return new IteratorIterator<>(Arrays.stream(arr).map(Iterable::iterator).iterator());
	}
	
	@SafeVarargs
	public static <T> IteratorIterator<T> ofArrayOfIterator(Iterator<T>... arr) {
		return new IteratorIterator<>(Arrays.asList(arr).iterator());
	}
	
	private final Iterator<Iterator<T>> iterators;
	
	private Iterator<T> currentIterator = null;
	
	private IteratorIterator(Iterator<Iterator<T>> iterators) {
		this.iterators = iterators;
	}
	
	private void fixCurrentIterator() {
		if (currentIterator != null && !currentIterator.hasNext()) {
			currentIterator = null;
		}
	}
	private void fixState() {
		fixCurrentIterator();
		while (currentIterator == null && iterators.hasNext()) {
			currentIterator = iterators.next();
			fixCurrentIterator();
		}
	}
	
	@Override
	public boolean hasNext() {
		fixState();
		return currentIterator != null && currentIterator.hasNext();
	}
	
	@Override
	public T next() {
		if (!hasNext())
			throw new NoSuchElementException("No next value found in iterator.");
		return currentIterator.next();
	}

	/**
	 * @implNote The current implementation of {@link IteratorIterator} may not support
	 * running this method if the current position is the last value of one of
	 * the underlying iterable, and if the {@link #hasNext()} method has been called before this one.
	 */
	@Override
	public void remove() {
		// TODO change code to avoid currentIterator being null because we are about to change currentIterator
		if (currentIterator != null)
			currentIterator.remove();
	}
}
