package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.paper.reflect.NMSReflect;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class DedicatedPlayerList extends PlayerList {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.dedicated.DedicatedPlayerList"));

    protected DedicatedPlayerList(Object obj) {
        super(obj);
    }
}
