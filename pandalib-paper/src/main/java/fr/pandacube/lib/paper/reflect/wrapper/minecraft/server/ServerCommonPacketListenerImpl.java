package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.protocol.Packet;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ServerCommonPacketListenerImpl extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.server.network.ServerCommonPacketListenerImpl"));
    public static final ReflectMethod<?> send = wrapEx(() -> REFLECT.method("send", Packet.REFLECT.get()));

    public void send(Packet packet) {
        wrapReflectEx(() -> send.invoke(__getRuntimeInstance(), unwrap(packet)));
    }

    protected ServerCommonPacketListenerImpl(Object obj) {
        super(obj);
    }
}
