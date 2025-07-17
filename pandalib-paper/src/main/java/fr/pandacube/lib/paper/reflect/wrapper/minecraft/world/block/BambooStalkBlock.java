package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.block;

import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.VoxelShape;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectField;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class BambooStalkBlock extends Block {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.level.block.BambooStalkBlock"));
    public static final ReflectField<?> SHAPE_COLLISION = wrapEx(() -> REFLECT.field("SHAPE_COLLISION"));

    public static VoxelShape SHAPE_COLLISION() {
        return wrap(wrapReflectEx(SHAPE_COLLISION::getStaticValue), VoxelShape.class);
    }

    public static void SHAPE_COLLISION(VoxelShape shape) {
        wrapReflectEx(() -> SHAPE_COLLISION.setStaticValue(unwrap(shape)));
    }

    protected BambooStalkBlock(Object obj) {
        super(obj);
    }
}
