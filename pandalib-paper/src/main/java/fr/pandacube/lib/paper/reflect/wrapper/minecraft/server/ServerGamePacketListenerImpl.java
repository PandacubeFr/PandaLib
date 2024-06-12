package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class ServerGamePacketListenerImpl extends ServerCommonPacketListenerImpl {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.server.network.ServerGamePacketListenerImpl"));

    protected ServerGamePacketListenerImpl(Object obj) {
        super(obj);
    }
}
