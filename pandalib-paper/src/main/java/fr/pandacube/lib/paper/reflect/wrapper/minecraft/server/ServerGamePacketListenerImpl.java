package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.protocol.Packet;
import fr.pandacube.lib.reflect.ReflectMethod;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ServerGamePacketListenerImpl extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.network.ServerGamePacketListenerImpl"));
    public static final ReflectMethod<?> send = wrapEx(() -> MAPPING.mojMethod("send", Packet.MAPPING));

    public void send(Packet packet) {
        wrapReflectEx(() -> send.invoke(__getRuntimeInstance(), unwrap(packet)));
    }

    protected ServerGamePacketListenerImpl(Object obj) {
        super(obj);
    }
}
