package fr.pandacube.lib.paper.reflect.wrapper.minecraft;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.NMSReflect.ClassMapping;
import fr.pandacube.lib.paper.reflect.wrapper.ConcreteWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapperI;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

@ConcreteWrapper(WorldVersion.__concrete.class)
public interface WorldVersion extends ReflectWrapperI {
    ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.WorldVersion"));



    class __concrete extends ReflectWrapper implements WorldVersion {
        private __concrete(Object obj) {
            super(obj);
        }
    }
}
