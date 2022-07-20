package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.block;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.VoxelShape;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class BambooBlock extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.level.block.BambooBlock"));
    public static final Reflect.ReflectField<?> COLLISION_SHAPE = wrapEx(() -> MAPPING.mojField("COLLISION_SHAPE"));

    public static VoxelShape COLLISION_SHAPE() {
        return wrap(wrapReflectEx(COLLISION_SHAPE::getStaticValue), VoxelShape.class);
    }

    public static void COLLISION_SHAPE(VoxelShape shape) {
        wrapReflectEx(() -> COLLISION_SHAPE.setStaticValue(unwrap(shape)));
    }

    protected BambooBlock(Object obj) {
        super(obj);
    }
}
