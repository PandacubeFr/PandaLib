package fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.protocol;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.protocol.custom.CustomPacketPayload;
import fr.pandacube.lib.reflect.ReflectConstructor;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ClientboundCustomPayloadPacket extends ReflectWrapper implements Packet {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket"));
    private static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> MAPPING.runtimeReflect().constructor(CustomPacketPayload.MAPPING.runtimeClass()));

    protected ClientboundCustomPayloadPacket(Object obj) {
        super(obj);
    }

    public ClientboundCustomPayloadPacket(CustomPacketPayload payload) {
        this(wrapReflectEx(() -> CONSTRUCTOR.instantiate(unwrap(payload))));
    }
}
