package fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands;

import com.mojang.brigadier.arguments.ArgumentType;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapperTyped;
import fr.pandacube.lib.reflect.ReflectMethod;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class EntityArgument extends ReflectWrapperTyped<ArgumentType<?>> {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.EntityArgument"));
    private static final ReflectMethod<?> entity = wrapEx(() -> MAPPING.mojMethod("entity"));
    private static final ReflectMethod<?> entities = wrapEx(() -> MAPPING.mojMethod("entities"));
    private static final ReflectMethod<?> player = wrapEx(() -> MAPPING.mojMethod("player"));
    private static final ReflectMethod<?> players = wrapEx(() -> MAPPING.mojMethod("players"));

    @SuppressWarnings("unchecked")
    public static ArgumentType<Object> entity() {
        return (ArgumentType<Object>) wrapReflectEx(() -> entity.invokeStatic());
    }

    @SuppressWarnings("unchecked")
    public static ArgumentType<Object> entities() {
        return (ArgumentType<Object>) wrapReflectEx(() -> entities.invokeStatic());
    }

    @SuppressWarnings("unchecked")
    public static ArgumentType<Object> player() {
        return (ArgumentType<Object>) wrapReflectEx(() -> player.invokeStatic());
    }

    @SuppressWarnings("unchecked")
    public static ArgumentType<Object> players() {
        return (ArgumentType<Object>) wrapReflectEx(() -> players.invokeStatic());
    }


    protected EntityArgument(Object obj) {
        super(obj);
    }
}
