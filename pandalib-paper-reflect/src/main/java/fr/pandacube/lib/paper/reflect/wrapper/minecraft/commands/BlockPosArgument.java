package fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands;

import com.mojang.brigadier.arguments.ArgumentType;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapperTyped;
import fr.pandacube.lib.reflect.ReflectMethod;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class BlockPosArgument extends ReflectWrapperTyped<ArgumentType<?>> {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.coordinates.BlockPosArgument"));
    private static final ReflectMethod<?> blockPos = wrapEx(() -> MAPPING.mojMethod("blockPos"));

    @SuppressWarnings("unchecked")
    public static ArgumentType<Object> blockPos() {
        return (ArgumentType<Object>) wrapReflectEx(() -> blockPos.invokeStatic());
    }

    protected BlockPosArgument(Object obj) {
        super(obj);
    }
}
