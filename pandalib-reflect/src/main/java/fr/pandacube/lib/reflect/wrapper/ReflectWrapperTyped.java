package fr.pandacube.lib.reflect.wrapper;

/**
 * Superclass of all reflect wrapper objects which wrapped objet type is statically known.
 * @param <T> the type (or supertype) of the wrapped object.
 */
public abstract class ReflectWrapperTyped<T> extends ReflectWrapper implements ReflectWrapperTypedI<T> {

    /**
     * Instantiate this reflection Wrapper with the provided object.
     * Any subclasses should not make their constructor public since the instanciation is managed by {@link #wrap(Object, Class) wrap(...)}.
     * @param obj the object to wrap. It must be an instance of the {@link #__getRuntimeClass() runtime class} of this
     *            wrapper class.
     */
    protected ReflectWrapperTyped(Object obj) {
        super(obj);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T __getRuntimeInstance() {
        return (T) super.__getRuntimeInstance();
    }
}
