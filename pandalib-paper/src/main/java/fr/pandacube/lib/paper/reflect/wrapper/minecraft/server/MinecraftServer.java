package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class MinecraftServer extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.MinecraftServer"));
    private static final ReflectMethod<?> getPlayerList = wrapEx(() -> MAPPING.mojMethod("getPlayerList"));

    public PlayerList getPlayerList() {
        return wrap(wrapReflectEx(() -> getPlayerList.invoke(__getRuntimeInstance())), PlayerList.class);
    }

    protected MinecraftServer(Object obj) {
        super(obj);
    }
}
