package fr.pandacube.lib.reflect.wrapper;

public abstract class ReflectWrapperTyped<T> extends ReflectWrapper implements ReflectWrapperTypedI<T> {

    protected ReflectWrapperTyped(Object obj) {
        super(obj);
    }

    @Override
    public Class<? extends T> __getRuntimeClass() {
        return ReflectWrapperTypedI.super.__getRuntimeClass();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T __getRuntimeInstance() {
        return (T) super.__getRuntimeInstance();
    }
}
