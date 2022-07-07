package fr.pandacube.lib.paper.reflect.wrapper.netty;

import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;

public abstract class ByteBuf extends ReflectWrapper {
    public static final Reflect.ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.netty.buffer.ByteBuf"));

    protected ByteBuf(Object obj) {
        super(obj);
    }


}
