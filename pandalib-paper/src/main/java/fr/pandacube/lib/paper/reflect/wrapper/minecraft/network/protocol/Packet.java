package fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.protocol;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.wrapper.ConcreteWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperI;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

@ConcreteWrapper(Packet.__concrete.class)
public interface Packet extends ReflectWrapperI {
    ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.network.protocol.Packet"));

    class __concrete extends ReflectWrapper implements Packet {
        protected __concrete(Object obj) {
            super(obj);
        }
    }
}
