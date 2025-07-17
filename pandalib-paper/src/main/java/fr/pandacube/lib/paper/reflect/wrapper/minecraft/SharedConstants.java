package fr.pandacube.lib.paper.reflect.wrapper.minecraft;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class SharedConstants extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.SharedConstants"));
    private static final ReflectMethod<?> getProtocolVersion = wrapEx(() -> REFLECT.method("getProtocolVersion"));



    public static int getProtocolVersion() {
        return (int) wrapReflectEx(() -> getProtocolVersion.invokeStatic());
    }




    protected SharedConstants(Object obj) {
        super(obj);
    }
}
