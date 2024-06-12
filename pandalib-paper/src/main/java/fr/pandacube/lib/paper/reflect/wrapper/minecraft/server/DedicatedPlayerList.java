package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class DedicatedPlayerList extends PlayerList {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.server.dedicated.DedicatedPlayerList"));

    protected DedicatedPlayerList(Object obj) {
        super(obj);
    }
}
