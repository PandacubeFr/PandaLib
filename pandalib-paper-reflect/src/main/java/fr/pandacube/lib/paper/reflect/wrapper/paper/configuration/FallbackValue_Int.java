package fr.pandacube.lib.paper.reflect.wrapper.paper.configuration;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class FallbackValue_Int extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.papermc.paper.configuration.type.fallback.FallbackValue$Int"));
    public static final ReflectMethod<?> value = wrapEx(() -> REFLECT.method("value"));

    public int value() {
        return (int) wrapReflectEx(() -> value.invoke(__getRuntimeInstance()));
    }

    protected FallbackValue_Int(Object obj) {
        super(obj);
    }
}
