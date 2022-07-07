package fr.pandacube.lib.paper.reflect.wrapper.paper;

import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.AABB;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.VoxelShape;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

public class AABBVoxelShape extends VoxelShape {
    public static final Reflect.ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.papermc.paper.voxel.AABBVoxelShape"));
    private static final Reflect.ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> REFLECT.constructor(AABB.MAPPING.runtimeClass()));

    public AABBVoxelShape(AABB aabb) {
        this(wrapReflectEx(() -> CONSTRUCTOR.instanciate(unwrap(aabb))));
    }

    protected AABBVoxelShape(Object obj) {
        super(obj);
    }
}
