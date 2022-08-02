package fr.pandacube.lib.paper.reflect.wrapper.minecraft;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.ReflectMethod;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class SharedConstants extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.SharedConstants"));
    private static final ReflectMethod<?> getCurrentVersion = wrapEx(() -> MAPPING.mojMethod("getCurrentVersion"));
    private static final ReflectMethod<?> getProtocolVersion = wrapEx(() -> MAPPING.mojMethod("getProtocolVersion"));



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
