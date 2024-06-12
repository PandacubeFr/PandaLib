package fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.chat;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.wrapper.ConcreteWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperI;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

@ConcreteWrapper(Component.__concrete.class)
public interface Component extends ReflectWrapperI {
    ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.network.chat.Component"));


    class __concrete extends ReflectWrapper implements Component {
        protected __concrete(Object obj) {
            super(obj);
        }
    }
}
