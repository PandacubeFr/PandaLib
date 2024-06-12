package fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.protocol.custom;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.wrapper.ConcreteWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperI;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

@ConcreteWrapper(CustomPacketPayload.__concrete.class)
public interface CustomPacketPayload extends ReflectWrapperI {
    ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.network.protocol.common.custom.CustomPacketPayload"));

    class __concrete extends ReflectWrapper implements CustomPacketPayload {
        protected __concrete(Object obj) {
            super(obj);
        }
    }
}
