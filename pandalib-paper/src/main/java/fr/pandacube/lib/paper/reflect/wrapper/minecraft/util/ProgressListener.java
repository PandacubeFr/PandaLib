package fr.pandacube.lib.paper.reflect.wrapper.minecraft.util;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.wrapper.ConcreteWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperI;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

@ConcreteWrapper(ProgressListener.__concrete.class)
public interface ProgressListener extends ReflectWrapperI {
    ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.util.ProgressListener"));


    class __concrete extends ReflectWrapper implements ProgressListener {
        protected __concrete(Object obj) {
            super(obj);
        }
    }
}
