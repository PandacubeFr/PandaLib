package fr.pandacube.lib.paper.reflect.wrapper.minecraft.util;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ConcreteWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapperI;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;

@ConcreteWrapper(ProgressListener.__concrete.class)
public interface ProgressListener extends ReflectWrapperI {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.util.ProgressListener"));


    public class __concrete extends ReflectWrapper implements ProgressListener {
        protected __concrete(Object obj) {
            super(obj);
        }
    }
}
