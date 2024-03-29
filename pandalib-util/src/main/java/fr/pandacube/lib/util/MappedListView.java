package fr.pandacube.lib.util;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

/**
 * A Wrapper list that provides a mapped view of the backend list. Every modification of this list will modify the
 * backend list. Each time a value is accessed or modified, the appropriate setter or getter is used to convert the
 * value between the source {@code S} and the visible {@code T} type.
 * @param <S> the source (backend) type
 * @param <T> the visible (mapped) type
 */
public class MappedListView<S, T> extends AbstractList<T> {

	/**
	 * The backend list of this {@link MappedListView}.
	 */
	protected final List<S> backend;
	private final Function<S, T> getter;
	private final Function<T, S> setter;
	
	/**
	 * Create a new wrapper for the provided backend list.
	 * @param backend the list backing this list
	 * @param getter the function converting the value from the backing list to the value of this list. It is used for
	 *               every operation involving reading data from the backing list and comparing existing data. For
	 *               instance, {@link #indexOf(Object)} iterate through the backing list, converting all the values with
	 *               {@code getter} before comparing them with the parameter of {@link #indexOf(Object)} before
	 *               comparing.
	 * @param setter used for modification of the data in the list (typically {@link #add(Object)} and
	 *               {@link #set(int, Object)} methods)
	 */
	public MappedListView(List<S> backend, Function<S, T> getter, Function<T, S> setter) {
		this.backend = backend;
		this.getter = getter;
		this.setter = setter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return backend.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T get(int index) {
		return getter.apply(backend.get(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T set(int index, T element) {
		return getter.apply(backend.set(index, setter.apply(element)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(T element) {
		return backend.add(setter.apply(element));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(int index, T element) {
		backend.add(index, setter.apply(element));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T remove(int index) {
		return getter.apply(backend.remove(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		backend.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return new MappedListView<>(backend.subList(fromIndex, toIndex), getter, setter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		backend.subList(fromIndex, toIndex).clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int indexOf(Object o) {
		return backend.indexOf(setter.apply((T) o));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int lastIndexOf(Object o) {
		return backend.lastIndexOf(setter.apply((T) o));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<T> iterator() {
		return new Iterator<>() {
			final Iterator<S> wrappedIt = backend.iterator();
			@Override
			public boolean hasNext() {
				return wrappedIt.hasNext();
			}

			@Override
			public T next() {
				return getter.apply(wrappedIt.next());
			}

			@Override
			public void remove() {
				wrappedIt.remove();
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListIterator<T> listIterator() {
		return listIterator(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListIterator<T> listIterator(int index) {
		return new ListIterator<>() {
			final ListIterator<S> wrappedIt = backend.listIterator(index);
			@Override
			public boolean hasNext() {
				return wrappedIt.hasNext();
			}

			@Override
			public T next() {
				return getter.apply(wrappedIt.next());
			}

			@Override
			public boolean hasPrevious() {
				return wrappedIt.hasPrevious();
			}

			@Override
			public T previous() {
				return getter.apply(wrappedIt.previous());
			}

			@Override
			public int nextIndex() {
				return wrappedIt.nextIndex();
			}

			@Override
			public int previousIndex() {
				return wrappedIt.previousIndex();
			}

			@Override
			public void remove() {
				wrappedIt.remove();
			}

			@Override
			public void set(T w) {
				wrappedIt.set(setter.apply(w));
			}

			@Override
			public void add(T w) {
				wrappedIt.add(setter.apply(w));
			}
		};
	}
	
	
	
}
