package fr.pandacube.lib.paper.reflect.wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import fr.pandacube.lib.core.util.MappedListView;

public class ReflectListWrapper<W extends ReflectWrapperI> extends MappedListView<Object, W> implements ReflectWrapperTypedI<List<Object>> {

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
