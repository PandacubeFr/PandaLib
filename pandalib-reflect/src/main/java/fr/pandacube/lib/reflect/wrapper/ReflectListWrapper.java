package fr.pandacube.lib.reflect.wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import fr.pandacube.lib.util.MappedListView;

/**
 * A wrapper for a list of wrapped object. It is an extension of {@link MappedListView} that is used to transparently
 * wrap/unwrap the elements of the backend list.
 * @param <W> the type of the reflect wrapper for the elements of this list.
 */
public class ReflectListWrapper<W extends ReflectWrapperI> extends MappedListView<Object, W> implements ReflectWrapperTypedI<List<Object>> {

    private final Class<W> expectedWrapperClass;

    /* package */ ReflectListWrapper(Class<W> expectedWrapperClass) {
        this(ArrayList::new, expectedWrapperClass);
    }


    @SuppressWarnings("unchecked")
    /* package */ ReflectListWrapper(Supplier<List<?>> listCreator, Class<W> expectedWrapperClass) {
        this((List<Object>) (listCreator == null ? new ArrayList<>() : listCreator.get()), expectedWrapperClass);
    }

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
}
