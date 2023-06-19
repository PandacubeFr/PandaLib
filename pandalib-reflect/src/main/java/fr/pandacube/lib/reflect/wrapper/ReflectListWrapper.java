package fr.pandacube.lib.reflect.wrapper;

import fr.pandacube.lib.util.MappedListView;

import java.util.List;

/**
 * A wrapper for a list of wrapped object. It is an extension of {@link MappedListView} that is used to transparently
 * wrap/unwrap the elements of the backend list.
 * @param <W> the type of the reflection wrapper for the elements of this list.
 */
public class ReflectListWrapper<W extends ReflectWrapperI> extends MappedListView<Object, W> implements ReflectWrapperTypedI<List<Object>> {

    private final Class<W> expectedWrapperClass;

    /* package */ ReflectListWrapper(List<Object> wrappedList, Class<W> expectedWrapperClass) {
        super(wrappedList, el -> ReflectWrapper.wrap(el, expectedWrapperClass), ReflectWrapper::unwrap);
        this.expectedWrapperClass = expectedWrapperClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<List<Object>> __getRuntimeClass() {
        return (Class<List<Object>>) backend.getClass();
    }

    @Override
    public List<Object> __getRuntimeInstance() {
        return backend;
    }

    @Override
    public List<W> subList(int fromIndex, int toIndex) {
        return new ReflectListWrapper<>(backend.subList(fromIndex, toIndex), expectedWrapperClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof List l && backend.equals(l instanceof ReflectListWrapper<?> rw ? rw.backend : l);
    }

    @Override
    public int hashCode() {
        return backend.hashCode();
    }
}
