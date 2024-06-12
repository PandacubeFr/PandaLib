package fr.pandacube.lib.paper.reflect.wrapper.minecraft.network;

import fr.pandacube.lib.paper.reflect.wrapper.netty.ByteBuf;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectConstructor;
import fr.pandacube.lib.reflect.ReflectMethod;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class FriendlyByteBuf extends ByteBuf {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.network.FriendlyByteBuf"));
    private static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> REFLECT.constructor(ByteBuf.REFLECT.get()));
    private static final ReflectMethod<?> writeUtf = wrapEx(() -> REFLECT.method("writeUtf", String.class));

    public FriendlyByteBuf(ByteBuf parent) {
        this(wrapReflectEx(() -> CONSTRUCTOR.instantiate(unwrap(parent))));
    }

    public FriendlyByteBuf writeUtf(String string) {
        return wrap(wrapReflectEx(() -> writeUtf.invoke(__getRuntimeInstance(), string)), FriendlyByteBuf.class);
    }

    protected FriendlyByteBuf(Object obj) {
        super(obj);
    }
}
