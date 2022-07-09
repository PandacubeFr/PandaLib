package fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapperTyped;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

public class Vec3Argument extends ReflectWrapperTyped<ArgumentType<?>> {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.coordinates.Vec3Argument"));
    private static final Reflect.ReflectMethod<?> vec3 = wrapEx(() -> MAPPING.mojMethod("vec3", boolean.class));

    @SuppressWarnings("unchecked")
    public static ArgumentType<Object> vec3(boolean centerIntegers) {
        return (ArgumentType<Object>) wrapReflectEx(() -> vec3.invokeStatic(centerIntegers));
    }


    protected Vec3Argument(Object obj) {
        super(obj);
    }
}
