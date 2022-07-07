package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.paper.reflect.NMSReflect;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;

public class DedicatedServerProperties extends Settings {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.dedicated.DedicatedServerProperties"));

    protected DedicatedServerProperties(Object obj) {
        super(obj);
    }
}
