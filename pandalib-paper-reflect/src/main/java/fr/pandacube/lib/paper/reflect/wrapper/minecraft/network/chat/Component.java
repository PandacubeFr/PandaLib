package fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.chat;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ConcreteWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapperI;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

@ConcreteWrapper(Component.__concrete.class)
public interface Component extends ReflectWrapperI {
    NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.network.chat.Component"));


    class __concrete extends ReflectWrapper implements Component {
        protected __concrete(Object obj) {
            super(obj);
        }
    }
}
