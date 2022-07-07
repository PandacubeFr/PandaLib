package fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapperTyped;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

public class EntityArgument extends ReflectWrapperTyped<ArgumentType<?>> {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.EntityArgument"));
    private static final Reflect.ReflectMethod<?> entity = wrapEx(() -> MAPPING.mojMethod("entity"));
    private static final Reflect.ReflectMethod<?> entities = wrapEx(() -> MAPPING.mojMethod("entities"));
    private static final Reflect.ReflectMethod<?> player = wrapEx(() -> MAPPING.mojMethod("player"));
    private static final Reflect.ReflectMethod<?> players = wrapEx(() -> MAPPING.mojMethod("players"));

    public static ArgumentType<?> entity() {
        return (ArgumentType<?>) wrapReflectEx(() -> entity.invokeStatic());
    }

    public static ArgumentType<?> entities() {
        return (ArgumentType<?>) wrapReflectEx(() -> entities.invokeStatic());
    }

    public static ArgumentType<?> player() {
        return (ArgumentType<?>) wrapReflectEx(() -> player.invokeStatic());
    }

    public static ArgumentType<?> players() {
        return (ArgumentType<?>) wrapReflectEx(() -> players.invokeStatic());
    }


    protected EntityArgument(Object obj) {
        super(obj);
    }
}
