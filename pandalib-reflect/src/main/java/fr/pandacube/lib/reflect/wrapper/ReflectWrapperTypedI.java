package fr.pandacube.lib.reflect.wrapper;

/**
 * Interface implemented by all reflect wrapper objects which wrapped objet type is statically known.
 * @param <T> the type (or supertype) of the wrapped object.
 */
public interface ReflectWrapperTypedI<T> extends ReflectWrapperI {
    @SuppressWarnings("unchecked")
    @Override
    default Class<? extends T> __getRuntimeClass() {
        return (Class<? extends T>) ReflectWrapperI.super.__getRuntimeClass();
    }

    @Override
    T __getRuntimeInstance();
}
