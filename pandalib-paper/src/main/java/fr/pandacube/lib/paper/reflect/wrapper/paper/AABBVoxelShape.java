package fr.pandacube.lib.paper.reflect.wrapper.paper;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.AABB;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.VoxelShape;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectConstructor;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class AABBVoxelShape extends VoxelShape {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.papermc.paper.voxel.AABBVoxelShape"));
    private static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> REFLECT.constructor(AABB.MAPPING.runtimeClass()));

    public AABBVoxelShape(AABB aabb) {
        this(wrapReflectEx(() -> CONSTRUCTOR.instantiate(unwrap(aabb))));
    }

    protected AABBVoxelShape(Object obj) {
        super(obj);
    }
}
