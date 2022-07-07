package fr.pandacube.lib.paper.reflect.wrapper.minecraft;

import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

public class SharedConstants extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.SharedConstants"));
    private static final Reflect.ReflectMethod<?> getCurrentVersion = wrapEx(() -> MAPPING.mojMethod("getCurrentVersion"));
    private static final Reflect.ReflectMethod<?> getProtocolVersion = wrapEx(() -> MAPPING.mojMethod("getProtocolVersion"));



    public static WorldVersion getCurrentVersion() {
        return wrap(wrapReflectEx(() -> getCurrentVersion.invokeStatic()), WorldVersion.class);
    }

    public static int getProtocolVersion() {
        return (int) wrapReflectEx(() -> getProtocolVersion.invokeStatic());
    }




    protected SharedConstants(Object obj) {
        super(obj);
    }
}
