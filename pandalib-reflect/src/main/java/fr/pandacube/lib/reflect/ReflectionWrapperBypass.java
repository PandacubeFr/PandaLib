package fr.pandacube.lib.reflect;

import fr.pandacube.lib.util.ThrowableUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Utility class to provide bypass functionality when the runtime environment modify our reflection code to inject a
 * wrapper that translate class and method names.
 * <p>
 * It is the case on 1.20.6+ Paper server: Paper runtime uses Mojang mapping but their reflection wrapper assumes the plugin
 * uses Spigot mapping, so tries to translate to Mojang names, but we already use the Mojang name.
 */
public class ReflectionWrapperBypass {

    private static final AtomicBoolean enabled = new AtomicBoolean(false);

    public static void enable() {
        enabled.set(true);
    }

    public static void disable() {
        enabled.set(false);
    }


    public static Class<?> getClass(String className) throws ClassNotFoundException {
        if (!enabled.get()) {
            return Class.forName(className);
        }

        try {
            Class<?> c = Class.forName(className);
            if (c.getName().equals(className)) // there were no translation here
                return c;
        } catch (ClassNotFoundException ignored) { }

        try {
            return (Class<?>) Class.class.getDeclaredMethod("forName", String.class)
                    .invoke(null, className);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError("java.lang.Class.forName(String)"); // wut?
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError("java.lang.Class.forName(String)"); // wut?
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof ClassNotFoundException cnfe)
                throw cnfe;
            throw ThrowableUtil.uncheck(e.getCause(), false);
        }
    }


}
