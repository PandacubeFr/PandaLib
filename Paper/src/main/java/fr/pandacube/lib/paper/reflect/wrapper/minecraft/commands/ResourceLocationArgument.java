package fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapperTyped;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

public class ResourceLocationArgument extends ReflectWrapperTyped<ArgumentType<?>> {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.ResourceLocationArgument"));
    private static final Reflect.ReflectMethod<?> id = wrapEx(() -> MAPPING.mojMethod("id"));

    public static ArgumentType<?> id() {
        return (ArgumentType<?>) wrapReflectEx(() -> id.invokeStatic());
    }

    protected ResourceLocationArgument(Object obj) {
        super(obj);
    }
}
