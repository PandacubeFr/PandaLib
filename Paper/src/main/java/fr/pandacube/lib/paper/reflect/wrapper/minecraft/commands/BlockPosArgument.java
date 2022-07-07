package fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapperTyped;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

public class BlockPosArgument extends ReflectWrapperTyped<ArgumentType<?>> {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.coordinates.BlockPosArgument"));
    private static final Reflect.ReflectMethod<?> blockPos = wrapEx(() -> MAPPING.mojMethod("blockPos"));

    public static ArgumentType<?> blockPos() {
        return (ArgumentType<?>) wrapReflectEx(() -> blockPos.invokeStatic());
    }

    protected BlockPosArgument(Object obj) {
        super(obj);
    }
}
