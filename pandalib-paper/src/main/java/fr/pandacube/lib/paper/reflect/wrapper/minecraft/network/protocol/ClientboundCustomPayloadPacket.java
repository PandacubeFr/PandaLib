package fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.protocol;

import fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.protocol.custom.CustomPacketPayload;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectConstructor;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ClientboundCustomPayloadPacket extends ReflectWrapper implements Packet {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket"));
    private static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> REFLECT.constructor(CustomPacketPayload.REFLECT.get()));

    protected ClientboundCustomPayloadPacket(Object obj) {
        super(obj);
    }

    public ClientboundCustomPayloadPacket(CustomPacketPayload payload) {
        this(wrapReflectEx(() -> CONSTRUCTOR.instantiate(unwrap(payload))));
    }
}
