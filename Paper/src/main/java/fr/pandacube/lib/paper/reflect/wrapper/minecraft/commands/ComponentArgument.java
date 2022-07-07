package fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapperTyped;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

public class ComponentArgument extends ReflectWrapperTyped<ArgumentType<?>> {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.ComponentArgument"));
    private static final Reflect.ReflectMethod<?> textComponent = wrapEx(() -> MAPPING.mojMethod("textComponent"));

    public static ArgumentType<?> textComponent() {
        return (ArgumentType<?>) wrapReflectEx(() -> textComponent.invokeStatic());
    }

    protected ComponentArgument(Object obj) {
        super(obj);
    }
}
