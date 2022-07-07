package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;

public class Vec3 extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.phys.Vec3"));

    protected Vec3(Object obj) {
        super(obj);
    }
}
