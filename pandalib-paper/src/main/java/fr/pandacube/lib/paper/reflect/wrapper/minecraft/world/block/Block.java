package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.block;

import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.VoxelShape;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class Block extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.level.block.Block"));
    private static final ReflectMethod<?> box = wrapEx(() -> REFLECT.method("box", double.class, double.class, double.class, double.class, double.class, double.class));


    public static VoxelShape box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return wrap(wrapReflectEx(() -> box.invokeStatic(minX, minY, minZ, maxX, maxY, maxZ)), VoxelShape.class);
    }

    protected Block(Object obj) {
        super(obj);
    }
}
