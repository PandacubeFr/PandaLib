package fr.pandacube.lib.paper.reflect.wrapper.netty;

import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

public class Unpooled extends ReflectWrapper {
    public static final Reflect.ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.netty.buffer.Unpooled"));
    private static final Reflect.ReflectMethod<?> buffer = wrapEx(() -> REFLECT.method("buffer"));


    public static ByteBuf buffer() {
        return wrap(wrapReflectEx(() -> buffer.invokeStatic()), ByteBuf.class);
    }



    protected Unpooled(Object obj) {
        super(obj);
    }
}
