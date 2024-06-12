package fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class GameProfileArgument extends ReflectWrapperTyped<ArgumentType<?>> {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.commands.arguments.GameProfileArgument"));
    private static final ReflectMethod<?> gameProfile = wrapEx(() -> REFLECT.method("gameProfile"));

    @SuppressWarnings("unchecked")
    public static ArgumentType<Object> gameProfile() {
        return (ArgumentType<Object>) wrapReflectEx(() -> gameProfile.invokeStatic());
    }

    protected GameProfileArgument(Object obj) {
        super(obj);
    }
}
