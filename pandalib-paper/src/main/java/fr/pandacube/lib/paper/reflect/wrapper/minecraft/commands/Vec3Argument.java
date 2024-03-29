package fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands;

import com.mojang.brigadier.arguments.ArgumentType;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;
import fr.pandacube.lib.reflect.ReflectMethod;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class Vec3Argument extends ReflectWrapperTyped<ArgumentType<?>> {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.coordinates.Vec3Argument"));
    private static final ReflectMethod<?> vec3 = wrapEx(() -> MAPPING.mojMethod("vec3", boolean.class));

    @SuppressWarnings("unchecked")
    public static ArgumentType<Object> vec3(boolean centerIntegers) {
        return (ArgumentType<Object>) wrapReflectEx(() -> vec3.invokeStatic(centerIntegers));
    }


    protected Vec3Argument(Object obj) {
        super(obj);
    }
}
