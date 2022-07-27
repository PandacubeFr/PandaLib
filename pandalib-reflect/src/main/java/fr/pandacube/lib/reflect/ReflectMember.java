package fr.pandacube.lib.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * Abstract wrapper for a class member (field, method or constructor).
 * @param <T> the type of the class declaring the wrapped member.
 * @param <ID> type of object uniquely identifying the member into the class.
 * @param <EL> the type of the class member (like {@link Field}, {@link Method} or {@link Constructor})
 * @param <EX> the type of exception thrown by the {@link Class} method responsible to get the member instance (like
 *             {@link NoSuchFieldException} or {@link NoSuchMethodException}).
 */
public sealed abstract class ReflectMember<T, ID, EL extends AccessibleObject & Member, EX extends ReflectiveOperationException>
        permits ReflectField, ReflectMethod, ReflectConstructor {

    /* package */ final ReflectClass<T> reflectClass;
    /* package */ final ID identifier;
    private final EL member;

    /* package */ ReflectMember(ReflectClass<T> c, ID id, boolean bypassFilter) throws EX {
        reflectClass = c;
        identifier = id;
        member = (bypassFilter) ? fetchFiltered() : fetch();
    }


    /* package */ EL fetch() throws EX {

        // get element in current class
        try {
            EL el = fetchFromClass(reflectClass.get());
            setAccessible(el);
            return el;
        } catch (ReflectiveOperationException e1) {
            @SuppressWarnings("unchecked")
            EX ex = (EX) e1;

            // get parent class
            Class<? super T> superClass = reflectClass.get().getSuperclass();
            if (superClass == null)
                throw ex;

            // get element in parent class (will do recursion)
            try {
                EL el = fetchFromReflectClass(Reflect.ofClass(superClass));
                setAccessible(el);
                return el;
            } catch (ReflectiveOperationException e2) {
                ex.addSuppressed(e2);
                throw ex;
            }
        }
    }

    /* package */ EL fetchFiltered() throws EX {

        // get element in current class
        try {
            EL el = fetchFromClass(reflectClass.get());
            setAccessible(el);
            return el;
        } catch (ReflectiveOperationException e1) {
            @SuppressWarnings("unchecked")
            EX ex = (EX) e1;

            // trying to bypass filtered member
            try {
                @SuppressWarnings("unchecked")
                EL[] elements = (EL[]) Reflect.ofClassOfInstance(reflectClass.get())
                        .method(internalMethodNameElementArray(), boolean.class)
                        .invoke(reflectClass.get(), false);
                for (EL element : elements) {
                    if (isEqualOurElement(element)) {
                        // the values in the elements array have to be copied
                        // (using special private methods in reflection api) before using it
                        Object reflectionFactoryOfClazz = Reflect.ofClassOfInstance(reflectClass.get())
                                .method("getReflectionFactory")
                                .invoke(reflectClass.get());
                        @SuppressWarnings("unchecked")
                        EL copiedElement = (EL) Reflect.ofClassOfInstance(reflectionFactoryOfClazz)
                                .method(internalMethodNameCopyElement(), element.getClass())
                                .invoke(reflectionFactoryOfClazz, element);
                        setAccessible(copiedElement);
                        return copiedElement;
                    }
                }
            } catch (ReflectiveOperationException e2) {
                ex.addSuppressed(e2);
            }

            throw ex;
        }
    }

    /* package */ abstract EL fetchFromClass(Class<T> clazz) throws EX;

    /* package */ abstract EL fetchFromReflectClass(ReflectClass<?> rc) throws EX;

    /* package */ abstract boolean isEqualOurElement(EL el);

    /* package */ abstract String internalMethodNameElementArray();

    /* package */ abstract String internalMethodNameCopyElement();

    /* package */ void setAccessible(EL el) {
        el.setAccessible(true);
    }

    /**
     * Returns the wrapped class member.
     * @return the wrapped class member.
     */
    public EL get() {
        return member;
    }

    /**
     * Returns the modifiers of the wrapped class member.
     * @return the modifiers of the wrapped class member.
     * @see Field#getModifiers()
     * @see Method#getModifiers()
     * @see Constructor#getModifiers()
     */
    public int getModifiers() {
        return get().getModifiers();
    }

}
