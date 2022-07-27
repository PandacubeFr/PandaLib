package fr.pandacube.lib.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Wrapper for a class {@link Constructor}.
 * @param <T> the type of the class declaring the wrapped constructor.
 */
public final class ReflectConstructor<T> extends ReflectMember<T, ConstructorIdentifier, Constructor<T>, NoSuchMethodException> {

    /* package */ ReflectConstructor(ReflectClass<T> c, ConstructorIdentifier constructorId, boolean bypassFilter) throws NoSuchMethodException {
        super(c, constructorId, bypassFilter);
    }

    // Override since we don't want to recursively search for a constructor
    @Override
    protected Constructor<T> fetch() throws NoSuchMethodException {
        Constructor<T> el = fetchFromClass(reflectClass.get());
        setAccessible(el);
        return el;
    }

    @Override
    protected Constructor<T> fetchFromClass(Class<T> clazz) throws NoSuchMethodException {
        return clazz.getDeclaredConstructor(identifier.parameters());
    }

    @Override
    protected Constructor<T> fetchFromReflectClass(ReflectClass<?> rc) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean isEqualOurElement(Constructor<T> el) {
        return Arrays.equals(identifier.parameters(), el.getParameterTypes());
    }

    @Override
    protected String internalMethodNameElementArray() {
        return "getDeclaredConstructors0";
    }

    @Override
    protected String internalMethodNameCopyElement() {
        return "copyConstructor";
    }

    /**
     * Invoke this constructor to create a new instance of the declaring class of this constructor.
     * @param values the parameters used for the constructor call.
     * @return the newly created instance.
     * @throws IllegalAccessException if the wrapped Constructor object is enforcing Java language access control and
     *                                the underlying constructor is inaccessible. Note that this
     *                                {@link ReflectConstructor} automatically sets the {@link Constructor}’s accessible
     *                                flag to true.
     * @throws IllegalArgumentException if there is any problem with the constructor parameters (wrong number, wrong
     *                                  type, ...).
     * @throws InstantiationException if the declaring class of this constructor is an abstract class.
     * @throws InvocationTargetException if the called constructor throws an exception.
     * @see Constructor#newInstance(Object...)
     */
    public T instanciate(Object... values) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return get().newInstance(values);
    }

}
