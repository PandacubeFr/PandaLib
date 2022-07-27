package fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands;

import com.mojang.brigadier.arguments.ArgumentType;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapperTyped;
import fr.pandacube.lib.reflect.ReflectMethod;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class GameProfileArgument extends ReflectWrapperTyped<ArgumentType<?>> {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.GameProfileArgument"));
    private static final ReflectMethod<?> gameProfile = wrapEx(() -> MAPPING.mojMethod("gameProfile"));

    @SuppressWarnings("unchecked")
    public static ArgumentType<Object> gameProfile() {
        return (ArgumentType<Object>) wrapReflectEx(() -> gameProfile.invokeStatic());
    }

    protected GameProfileArgument(Object obj) {
        super(obj);
    }
}
