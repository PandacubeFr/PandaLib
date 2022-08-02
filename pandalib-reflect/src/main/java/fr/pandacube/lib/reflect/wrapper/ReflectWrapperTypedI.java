package fr.pandacube.lib.reflect.wrapper;

public interface ReflectWrapperTypedI<T> extends ReflectWrapperI {
    @SuppressWarnings("unchecked")
    @Override
    default Class<? extends T> __getRuntimeClass() {
        return (Class<? extends T>) ReflectWrapperI.super.__getRuntimeClass();
    }

    @Override
    T __getRuntimeInstance();
}
