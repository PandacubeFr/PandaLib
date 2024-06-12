package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class DedicatedServer extends MinecraftServer {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.server.dedicated.DedicatedServer"));
    private static final ReflectMethod<?> getLevelIdName = wrapEx(() -> REFLECT.method("getLevelIdName"));
    private static final ReflectMethod<?> getProperties = wrapEx(() -> REFLECT.method("getProperties"));

    public String getLevelIdName() {
        return (String) wrapReflectEx(() -> getLevelIdName.invoke(__getRuntimeInstance()));
    }

    public DedicatedServerProperties getProperties() {
        return wrap(wrapReflectEx(() -> getProperties.invoke(__getRuntimeInstance())), DedicatedServerProperties.class);
    }

    protected DedicatedServer(Object obj) {
        super(obj);
    }
}
