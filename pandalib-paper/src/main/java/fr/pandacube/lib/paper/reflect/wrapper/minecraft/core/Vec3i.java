package fr.pandacube.lib.paper.reflect.wrapper.minecraft.core;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class Vec3i extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.core.Vec3i"));
    public static final ReflectMethod<?> getX = wrapEx(() -> REFLECT.method("getX"));
    public static final ReflectMethod<?> getY = wrapEx(() -> REFLECT.method("getY"));
    public static final ReflectMethod<?> getZ = wrapEx(() -> REFLECT.method("getZ"));

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
