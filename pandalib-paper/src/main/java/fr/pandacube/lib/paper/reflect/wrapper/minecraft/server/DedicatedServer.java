package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class DedicatedServer extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.server.dedicated.DedicatedServer"));
    private static final ReflectMethod<?> getLevelIdName = wrapEx(() -> REFLECT.method("getLevelIdName"));

    public String getLevelIdName() {
        return (String) wrapReflectEx(() -> getLevelIdName.invoke(__getRuntimeInstance()));
    }

    protected DedicatedServer(Object obj) {
        super(obj);
    }
}
