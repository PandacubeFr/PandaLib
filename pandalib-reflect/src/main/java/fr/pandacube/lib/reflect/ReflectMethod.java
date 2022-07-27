package fr.pandacube.lib.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Wrapper for a class {@link Method}.
 * @param <T> the type of the class declaring the wrapped method.
 */
public final class ReflectMethod<T> extends ReflectMember<T, MethodIdentifier, Method, NoSuchMethodException> {

    /* package */ ReflectMethod(ReflectClass<T> c, MethodIdentifier methodId, boolean bypassFilter) throws NoSuchMethodException {
        super(c, methodId, bypassFilter);
    }

    @Override
    protected Method fetchFromClass(Class<T> clazz) throws NoSuchMethodException {
        return clazz.getDeclaredMethod(identifier.methodName(), identifier.parameters());
    }

    @Override
    protected Method fetchFromReflectClass(ReflectClass<?> rc) throws NoSuchMethodException {
        return rc.method(identifier, false).get();
    }

    @Override
    protected boolean isEqualOurElement(Method el) {
        return identifier.methodName().equals(el.getName()) && Arrays.equals(identifier.parameters(), el.getParameterTypes());
    }

    @Override
    protected String internalMethodNameElementArray() {
        return "getDeclaredMethods0";
    }

    @Override
    protected String internalMethodNameCopyElement() {
        return "copyMethod";
    }

    /**
     * Invokes this method on the specified instance with the specified parameters.
     * @param instance the instance on which to call the method.
     * @param values the parameters used for the method call.
     * @return the eventual return value of the method call.
     * @throws IllegalAccessException if the wrapped Method object is enforcing Java language access control and the
     *                                underlying method is inaccessible. Note that this {@link ReflectMethod}
     *                                automatically sets the {@link Method}’s accessible flag to true.
     * @throws IllegalArgumentException if the specified instance is not an instance of the class or interface declaring
     *                                  the wrapped method (or a subclass or implementor thereof), or if there is any
     *                                  problem with the method parameters (wrong number, wrong type, ...).
     * @throws NullPointerException if the specified instance is null and the field is an instance field.
     * @throws InvocationTargetException if the called method throws an exception.
     * @see Method#invoke(Object, Object...)
     */
    public Object invoke(Object instance, Object... values) throws InvocationTargetException, IllegalAccessException {
        return get().invoke(instance, values);
    }

    /**
     * Invokes this static method with the specified parameters.
     * @param values the parameters used for the method call.
     * @return the eventual return value of the method call.
     * @throws IllegalAccessException if the wrapped Method object is enforcing Java language access control and the
     *                                underlying method is inaccessible. Note that this {@link ReflectMethod}
     *                                automatically sets the {@link Method}’s accessible flag to true.
     * @throws IllegalArgumentException if there is any problem with the method parameters (wrong number, wrong type,
     *                                  ...).
     * @throws NullPointerException if the wrapped {@link Method} is actually an instance method. In this case,
     *                              {@link #invoke(Object, Object...)} should be called instead with a non-null first
     *                              parameter.
     * @throws InvocationTargetException if the called method throws an exception.
     * @see Method#invoke(Object, Object...)
     */
    public Object invokeStatic(Object... values) throws InvocationTargetException, IllegalAccessException {
        return invoke(null, values);
    }

}
