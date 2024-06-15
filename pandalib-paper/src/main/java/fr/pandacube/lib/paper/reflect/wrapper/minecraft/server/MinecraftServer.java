package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.paper.reflect.wrapper.minecraft.core.RegistryAccess;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class MinecraftServer extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.server.MinecraftServer"));
    private static final ReflectMethod<?> getPlayerList = wrapEx(() -> REFLECT.method("getPlayerList"));
    public static final ReflectMethod<?> registryAccess = wrapEx(() -> REFLECT.method("registryAccess"));

    public PlayerList getPlayerList() {
        return wrap(wrapReflectEx(() -> getPlayerList.invoke(__getRuntimeInstance())), PlayerList.class);
    }

    public RegistryAccess registryAccess() {
        return wrap(wrapReflectEx(() -> registryAccess.invoke(__getRuntimeInstance())), RegistryAccess.class);
    }

    protected MinecraftServer(Object obj) {
        super(obj);
    }
}
