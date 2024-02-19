package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.paper.reflect.NMSReflect;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class ServerGamePacketListenerImpl extends ServerCommonPacketListenerImpl {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.network.ServerGamePacketListenerImpl"));

    protected ServerGamePacketListenerImpl(Object obj) {
        super(obj);
    }
}
