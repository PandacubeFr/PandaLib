package fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.chat;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ConcreteWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapperI;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;

@ConcreteWrapper(Component.__concrete.class)
public interface Component extends ReflectWrapperI {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.network.chat.Component"));


    public class __concrete extends ReflectWrapper implements Component {
        protected __concrete(Object obj) {
            super(obj);
        }
    }
}
