package fr.pandacube.lib.paper.reflect.wrapper.minecraft.core;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class BlockPos extends Vec3i {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.core.BlockPos"));

    protected BlockPos(Object obj) {
        super(obj);
    }
}
