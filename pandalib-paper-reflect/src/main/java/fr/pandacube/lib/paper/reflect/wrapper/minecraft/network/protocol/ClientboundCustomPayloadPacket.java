package fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.protocol;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.FriendlyByteBuf;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.resources.ResourceLocation;
import fr.pandacube.lib.reflect.ReflectConstructor;
import fr.pandacube.lib.reflect.ReflectField;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ClientboundCustomPayloadPacket extends ReflectWrapper implements Packet {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket"));
    private static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> MAPPING.runtimeReflect().constructor(ResourceLocation.MAPPING.runtimeClass(), FriendlyByteBuf.MAPPING.runtimeClass()));
    private static final ReflectField<?> FIELD_BRAND = wrapEx(() -> MAPPING.mojField("BRAND"));

    public static ResourceLocation BRAND() {
        return wrap(wrapReflectEx(FIELD_BRAND::getStaticValue), ResourceLocation.class);
    }

    protected ClientboundCustomPayloadPacket(Object obj) {
        super(obj);
    }

    public ClientboundCustomPayloadPacket(ResourceLocation res, FriendlyByteBuf buff) {
        this(wrapReflectEx(() -> CONSTRUCTOR.instanciate(unwrap(res), unwrap(buff))));
    }
}
