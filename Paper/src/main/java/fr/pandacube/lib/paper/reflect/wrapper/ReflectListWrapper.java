package fr.pandacube.lib.paper.reflect.wrapper;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Supplier;

import static fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper.unwrap;

public class ReflectListWrapper<W extends ReflectWrapperI> extends AbstractList<W> implements ReflectWrapperTypedI<List<Object>> {

    private final List<Object> wrappedList;
    private final Class<W> expectedWrapperClass;

    /* package */ ReflectListWrapper(Class<W> expectedWrapperClass) {
        this(ArrayList::new, expectedWrapperClass);
    }

    /* package */
    @SuppressWarnings("unchecked")
    ReflectListWrapper(Supplier<List<?>> listCreator, Class<W> expectedWrapperClass) {
        this((List<Object>) (listCreator == null ? new ArrayList<>() : listCreator.get()), expectedWrapperClass);
    }
    /* package */ ReflectListWrapper(List<Object> wrappedList, Class<W> expectedWrapperClass) {
        this.wrappedList = Objects.requireNonNull(wrappedList);
        this.expectedWrapperClass = expectedWrapperClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<List<Object>> __getRuntimeClass() {
        return (Class<List<Object>>) wrappedList.getClass();
    }

    @Override
    public List<Object> __getRuntimeInstance() {
        return wrappedList;
    }

    private W wrap(Object el) {
        return ReflectWrapper.wrap(el, expectedWrapperClass);
    }

    @Override
    public W get(int index) {
        return wrap(wrappedList.get(index));
    }

    @Override
    public int size() {
        return wrappedList.size();
    }

    @Override
    public boolean add(W w) {
        return wrappedList.add(unwrap(w));
    }

    @Override
    public W set(int index, W element) {
        return wrap(wrappedList.set(index, unwrap(element)));
    }

    @Override
    public void add(int index, W element) {
        wrappedList.add(index, unwrap(element));
    }

    @Override
    public W remove(int index) {
        return wrap(wrappedList.remove(index));
    }

    @Override
    public int indexOf(Object o) {
        return wrappedList.indexOf(o instanceof ReflectWrapperI w ? w.__getRuntimeInstance() : o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return wrappedList.lastIndexOf(o instanceof ReflectWrapperI w ? w.__getRuntimeInstance() : o);
    }

    @Override
    public void clear() {
        wrappedList.clear();
    }

    @Override
    public Iterator<W> iterator() {
        return new Iterator<W>() {
            final Iterator<Object> wrappedIt = wrappedList.iterator();
            @Override
            public boolean hasNext() {
                return wrappedIt.hasNext();
            }

            @Override
            public W next() {
                return wrap(wrappedIt.next());
            }

            @Override
            public void remove() {
                wrappedIt.remove();
            }
        };
    }

    @Override
    public ListIterator<W> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<W> listIterator(int index) {
        return new ListIterator<W>() {
            final ListIterator<Object> wrappedIt = wrappedList.listIterator(index);
            @Override
            public boolean hasNext() {
                return wrappedIt.hasNext();
            }

            @Override
            public W next() {
                return wrap(wrappedIt.next());
            }

            @Override
            public boolean hasPrevious() {
                return wrappedIt.hasPrevious();
            }

            @Override
            public W previous() {
                return wrap(wrappedIt.previous());
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
            public void set(W w) {
                wrappedIt.set(unwrap(w));
            }

            @Override
            public void add(W w) {
                wrappedIt.add(unwrap(w));
            }
        };
    }

    @Override
    public List<W> subList(int fromIndex, int toIndex) {
        return new ReflectListWrapper<>(wrappedList.subList(fromIndex, toIndex), expectedWrapperClass);
    }

    @Override
    public boolean equals(Object o) {
        return wrappedList.equals(o);
    }

    @Override
    public int hashCode() {
        return wrappedList.hashCode();
    }
}
