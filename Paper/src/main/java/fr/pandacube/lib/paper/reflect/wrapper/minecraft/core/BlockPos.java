package fr.pandacube.lib.paper.reflect.wrapper.minecraft.core;

import fr.pandacube.lib.paper.reflect.NMSReflect;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;

public class BlockPos extends Vec3i {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.core.BlockPos"));

    protected BlockPos(Object obj) {
        super(obj);
    }
}
