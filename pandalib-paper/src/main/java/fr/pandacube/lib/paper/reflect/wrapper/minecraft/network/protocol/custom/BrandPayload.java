package fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.protocol.custom;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectConstructor;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class BrandPayload extends ReflectWrapper implements CustomPacketPayload {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.network.protocol.common.custom.BrandPayload"));
    private static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> REFLECT.constructor(String.class));

    public BrandPayload(String brand) {
        this(wrapReflectEx(() -> CONSTRUCTOR.instantiate(brand)));
    }

    protected BrandPayload(Object obj) {
        super(obj);
    }
}
