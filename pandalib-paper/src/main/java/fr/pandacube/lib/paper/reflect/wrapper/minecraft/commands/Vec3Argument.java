package fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class Vec3Argument extends ReflectWrapperTyped<ArgumentType<?>> {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.commands.arguments.coordinates.Vec3Argument"));
    private static final ReflectMethod<?> vec3 = wrapEx(() -> REFLECT.method("vec3", boolean.class));

    @SuppressWarnings("unchecked")
    public static ArgumentType<Object> vec3(boolean centerIntegers) {
        return (ArgumentType<Object>) wrapReflectEx(() -> vec3.invokeStatic(centerIntegers));
    }


    protected Vec3Argument(Object obj) {
        super(obj);
    }
}
