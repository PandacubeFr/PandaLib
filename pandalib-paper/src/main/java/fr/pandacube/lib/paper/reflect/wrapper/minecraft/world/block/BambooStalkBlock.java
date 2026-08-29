package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.block;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectField;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class BambooStalkBlock extends Block {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.level.block.BambooStalkBlock"));
    public static final ReflectField<?> SHAPE_COLLISION = wrapEx(() -> REFLECT.field("SHAPE_COLLISION")); // directly used without wrapper value

    protected BambooStalkBlock(Object obj) {
        super(obj);
    }
}
