package fr.pandacube.lib.paper.reflect.wrapper.netty;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.ReflectClass;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class ByteBuf extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.netty.buffer.ByteBuf"));

    protected ByteBuf(Object obj) {
        super(obj);
    }


}
