package fr.pandacube.lib.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Wrapper for a class {@link Field}.
 * @param <T> the type of the class declaring the wrapped field.
 */
public final class ReflectField<T> extends ReflectMember<T, String, Field, NoSuchFieldException> {

    /* Those fields are used to modify the value of a static variable. Depending on the current Java version,
     * one of them will be used for this purpose.
     */
    private static sun.misc.Unsafe sunMiscUnsafeInstance;
    private static Field modifiersFieldInFieldClass;
    static {
        RuntimeException ex = null;
        try {
            sunMiscUnsafeInstance = Runtime.version().feature() >= 16
                    ? (sun.misc.Unsafe) Reflect.ofClass("sun.misc.Unsafe")
                            .field("theUnsafe")
                            .getStaticValue()
                    : null;
        } catch (Exception e) {
            ex = new RuntimeException("Cannot access to sun.misc.Unsafe.theUnsafe value.", e);
        }

        try {
            @SuppressWarnings("deprecation")
            Field f = Runtime.version().feature() < 16
                    ? Reflect.ofClass(Field.class).filteredField("modifiers").get()
                    : null;
            modifiersFieldInFieldClass = f;
        } catch (Exception e) {
            RuntimeException newEx = new RuntimeException("Cannot access " + Field.class + ".modifiers field.", e);
            if (ex != null)
                newEx.addSuppressed(ex);
            ex = newEx;
        }

        if (ex != null)
            throw ex;
    }

    /* package */ ReflectField(ReflectClass<T> c, String name, boolean bypassFilter) throws NoSuchFieldException {
        super(c, name, bypassFilter);
    }

    @Override
    /* package */ Field fetchFromClass(Class<T> clazz) throws NoSuchFieldException {
        return clazz.getDeclaredField(identifier);
    }

    @Override
    /* package */ Field fetchFromReflectClass(ReflectClass<?> rc) throws NoSuchFieldException {
        return rc.field(identifier).get();
    }

    @Override
    /* package */ boolean isEqualOurElement(Field el) {
        return identifier.equals(el.getName());
    }

    @Override
    /* package */ String internalMethodNameElementArray() {
        return "getDeclaredFields0";
    }

    @Override
    /* package */ String internalMethodNameCopyElement() {
        return "copyField";
    }

    /**
     * Returns the value of this field, in the provided instance.
     * @param instance the instance in which to get the value of the field.
     * @return the value of this field, in the provided instance.
     * @throws IllegalAccessException if the wrapped Field object is enforcing Java language access control and the
     *                                underlying field is inaccessible. Note that this {@link ReflectField}
     *                                automatically sets the {@link Field}’s accessible flag to true.
     * @throws IllegalArgumentException if the specified instance is not an instance of the class or interface declaring
     *                                  the wrapped field (or a subclass or implementor thereof).
     * @throws NullPointerException if the specified instance is null and the field is an instance field.
     * @see Field#get(Object)
     */
    public Object getValue(Object instance) throws IllegalAccessException {
        return get().get(instance);
    }

    /**
     * Returns the value of this static field.
     * @return the value of this static field.
     * @throws IllegalAccessException if the wrapped Field object is enforcing Java language access control and the
     *                                underlying field is inaccessible. Note that this {@link ReflectField}
     *                                automatically sets the {@link Field}’s accessible flag to true.
     * @throws NullPointerException if the wrapped {@link Field} is actually an instance field. In this case,
     *                              {@link #getValue(Object)} should be called instead with a non-null parameter.
     * @see Field#get(Object)
     */
    public Object getStaticValue() throws IllegalAccessException {
        return getValue(null);
    }

    /**
     * Sets the value of this field, in the provided instance.
     * @param instance the instance in which to set the value of the field.
     * @param value the new value for this field.
     * @throws IllegalAccessException if the wrapped Field object is enforcing Java language access control and the
     *                                underlying field is inaccessible. Note that this {@link ReflectField}
     *                                automatically sets the {@link Field}’s accessible flag to true.
     * @throws IllegalArgumentException if the specified instance is not an instance of the class or interface declaring
     *                                  the wrapped field (or a subclass or implementor thereof).
     * @throws NullPointerException if the specified instance is null and the field is an instance field.
     * @see Field#set(Object, Object)
     */
    public void setValue(Object instance, Object value) throws IllegalAccessException {

        Field f = get();
        int realModifiers = f.getModifiers();
        if (Modifier.isFinal(realModifiers)) {
            // if the field is final, we have to do some unsafe stuff :/
            if (sunMiscUnsafeInstance != null) { // Java >= 16
                // set the value of the field, directly in the memory
                if (Modifier.isStatic(realModifiers)) {
                    long offset = sunMiscUnsafeInstance.staticFieldOffset(f);
                    sunMiscUnsafeInstance.putObject(sunMiscUnsafeInstance.staticFieldBase(f), offset, value);
                } else {
                    long offset = sunMiscUnsafeInstance.objectFieldOffset(f);
                    sunMiscUnsafeInstance.putObject(instance, offset, value);
                }
            } else { // Java < 16
                // change the modifier in the Field instance so the method #set(instance, value) doesn't throw an exception
                try {
                    modifiersFieldInFieldClass.set(f, realModifiers & ~Modifier.FINAL);
                    f.set(instance, value);
                } finally {
                    modifiersFieldInFieldClass.set(f, realModifiers);
                }
            }
        } else { // not final value
            f.set(instance, value);
        }
    }

    /**
     * Sets the value of this static field.
     * @param value the new value for this field.
     * @throws IllegalAccessException if the wrapped Field object is enforcing Java language access control and the
     *                                underlying field is inaccessible. Note that this {@link ReflectField}
     *                                automatically sets the {@link Field}’s accessible flag to true.
     * @throws NullPointerException if the wrapped {@link Field} is actually an instance field. In this case,
     *                              {@link #setValue(Object, Object)} should be called instead with a non-null parameter.
     * @see Field#set(Object, Object)
     */
    public void setStaticValue(Object value) throws IllegalAccessException {
        setValue(null, value);
    }

}
