package fr.pandacube.lib.paper.reflect.wrapper.craftbukkit;

import fr.pandacube.lib.paper.reflect.OBCReflect;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.DedicatedPlayerList;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.DedicatedServer;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;

import org.bukkit.Server;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class CraftServer extends ReflectWrapperTyped<Server> {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("CraftServer"));
    public static final ReflectMethod<?> getServer = wrapEx(() -> REFLECT.method("getServer"));
    public static final ReflectMethod<?> getHandle = wrapEx(() -> REFLECT.method("getHandle"));

    public DedicatedServer getServer() {
        return wrap(wrapReflectEx(() -> getServer.invoke(__getRuntimeInstance())), DedicatedServer.class);
    }

    public DedicatedPlayerList getHandle() {
        return wrap(wrapReflectEx(() -> getHandle.invoke(__getRuntimeInstance())), DedicatedPlayerList.class);
    }

    protected CraftServer(Object obj) {
        super(obj);
    }
}
