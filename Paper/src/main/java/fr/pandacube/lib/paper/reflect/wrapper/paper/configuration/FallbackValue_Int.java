package fr.pandacube.lib.paper.reflect.wrapper.paper.configuration;

import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

public class FallbackValue_Int extends ReflectWrapper {
    public static final Reflect.ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.papermc.paper.configuration.type.fallback.FallbackValue$Int"));
    public static final Reflect.ReflectMethod<?> value = wrapEx(() -> REFLECT.method("value"));

    public int value() {
        return (int) wrapReflectEx(() -> value.invoke(__getRuntimeInstance()));
    }

    protected FallbackValue_Int(Object obj) {
        super(obj);
    }
}
