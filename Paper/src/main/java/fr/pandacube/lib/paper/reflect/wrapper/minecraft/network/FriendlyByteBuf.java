package fr.pandacube.lib.paper.reflect.wrapper.minecraft.network;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.netty.ByteBuf;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class FriendlyByteBuf extends ByteBuf {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.network.FriendlyByteBuf"));
    private static final Reflect.ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> MAPPING.runtimeReflect().constructor(ByteBuf.REFLECT.get()));
    private static final Reflect.ReflectMethod<?> writeUtf = wrapEx(() -> MAPPING.mojMethod("writeUtf", String.class));

    public FriendlyByteBuf(ByteBuf parent) {
        this(wrapReflectEx(() -> CONSTRUCTOR.instanciate(unwrap(parent))));
    }

    public FriendlyByteBuf writeUtf(String string) {
        return wrap(wrapReflectEx(() -> writeUtf.invoke(__getRuntimeInstance(), string)), FriendlyByteBuf.class);
    }

    protected FriendlyByteBuf(Object obj) {
        super(obj);
    }
}
