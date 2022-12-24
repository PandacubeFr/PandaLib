package fr.pandacube.lib.paper.reflect.wrapper.minecraft.core;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.ReflectMethod;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class Vec3i extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.core.Vec3i"));
    public static final ReflectMethod<?> getX = wrapEx(() -> MAPPING.mojMethod("getX"));
    public static final ReflectMethod<?> getY = wrapEx(() -> MAPPING.mojMethod("getY"));
    public static final ReflectMethod<?> getZ = wrapEx(() -> MAPPING.mojMethod("getZ"));

    public int getX() {
        return (int) wrapReflectEx(() -> getX.invoke(__getRuntimeInstance()));
    }

    public int getY() {
        return (int) wrapReflectEx(() -> getY.invoke(__getRuntimeInstance()));
    }

    public int getZ() {
        return (int) wrapReflectEx(() -> getZ.invoke(__getRuntimeInstance()));
    }

    protected Vec3i(Object obj) {
        super(obj);
    }
}
