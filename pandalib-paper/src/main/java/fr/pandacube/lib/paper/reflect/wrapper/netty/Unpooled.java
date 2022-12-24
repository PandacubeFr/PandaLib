package fr.pandacube.lib.paper.reflect.wrapper.netty;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class Unpooled extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.netty.buffer.Unpooled"));
    private static final ReflectMethod<?> buffer = wrapEx(() -> REFLECT.method("buffer"));


    public static ByteBuf buffer() {
        return wrap(wrapReflectEx(() -> buffer.invokeStatic()), ByteBuf.class);
    }



    protected Unpooled(Object obj) {
        super(obj);
    }
}
