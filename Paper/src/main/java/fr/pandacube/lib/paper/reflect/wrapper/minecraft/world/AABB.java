package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

public class AABB extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.phys.AABB"));
    private static final Reflect.ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> MAPPING.runtimeReflect().constructor(double.class, double.class, double.class, double.class, double.class, double.class));

    public AABB(double x1, double y1, double z1, double x2, double y2, double z2) {
        this(wrapReflectEx(() -> CONSTRUCTOR.instanciate(x1, y1, z1, x2, y2, z2)));
    }

    protected AABB(Object obj) {
        super(obj);
    }
}
