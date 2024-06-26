package fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ResourceLocationArgument extends ReflectWrapperTyped<ArgumentType<?>> {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.commands.arguments.ResourceLocationArgument"));
    private static final ReflectMethod<?> id = wrapEx(() -> REFLECT.method("id"));

    @SuppressWarnings("unchecked")
    public static ArgumentType<Object> id() {
        return (ArgumentType<Object>) wrapReflectEx(() -> id.invokeStatic());
    }

    protected ResourceLocationArgument(Object obj) {
        super(obj);
    }
}
