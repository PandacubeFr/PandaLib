package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.reflect.ReflectMethod;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class DedicatedServer extends MinecraftServer {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.dedicated.DedicatedServer"));
    private static final ReflectMethod<?> getLevelIdName = wrapEx(() -> MAPPING.mojMethod("getLevelIdName"));
    private static final ReflectMethod<?> getProperties = wrapEx(() -> MAPPING.mojMethod("getProperties"));

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
