package fr.pandacube.lib.core.util;

import java.util.AbstractList;
import java.util.List;
import java.util.function.Function;

public class MappedListView<S, T> extends AbstractList<T> {
	
	private final List<S> backend;
	private final Function<S, T> getter;
	private final Function<T, S> setter;
	
	/**
	 * 
	 * @param backend the list backing this list
	 * @param getter the function converting the value from the backing list to the value of this list.
	 * It is used for every operation involving reading data from the backing list and comparing existing data.
	 * For instance, {@link #indexOf(Object)} iterate through the backing list, converting all the values
	 * with {@code getter} before comparing them with the parameter of {@link #indexOf(Object)}.
	 * before comparing
	 * @param setter used for modification of the data in the list (typically {@code add} and {@code set} methods)
	 */
	public MappedListView(List<S> backend, Function<S, T> getter, Function<T, S> setter) {
		this.backend = backend;
		this.getter = getter;
		this.setter = setter;
	}
	
	
	@Override
	public int size() { return backend.size(); }
	@Override
	public T get(int index) { return getter.apply(backend.get(index)); }
	@Override
	public T set(int index, T element) { return getter.apply(backend.set(index, setter.apply(element))); }
	@Override
	public void add(int index, T element) { backend.add(index, setter.apply(element)); }
	@Override
	public T remove(int index) { return getter.apply(backend.remove(index)); }
	@Override
	public void clear() { backend.clear(); }
	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return new MappedListView<S, T>(backend.subList(fromIndex, toIndex), getter, setter);
	}
	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		backend.subList(fromIndex, toIndex).clear();
	}
	
	
	
}
