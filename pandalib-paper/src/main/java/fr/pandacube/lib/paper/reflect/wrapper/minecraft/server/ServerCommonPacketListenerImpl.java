package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.protocol.Packet;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ServerCommonPacketListenerImpl extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.network.ServerCommonPacketListenerImpl"));
    public static final ReflectMethod<?> send = wrapEx(() -> MAPPING.mojMethod("send", Packet.MAPPING));

    public void send(Packet packet) {
        wrapReflectEx(() -> send.invoke(__getRuntimeInstance(), unwrap(packet)));
    }

    protected ServerCommonPacketListenerImpl(Object obj) {
        super(obj);
    }
}
