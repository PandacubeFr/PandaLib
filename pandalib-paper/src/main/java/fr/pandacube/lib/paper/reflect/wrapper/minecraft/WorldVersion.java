package fr.pandacube.lib.paper.reflect.wrapper.minecraft;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.NMSReflect.ClassMapping;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.DataVersion;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ConcreteWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperI;

import static fr.pandacube.lib.reflect.wrapper.ReflectWrapper.wrap;
import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

@ConcreteWrapper(WorldVersion.__concrete.class)
public interface WorldVersion extends ReflectWrapperI {
    ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.WorldVersion"));
    ReflectMethod<?> getDataVersion = wrapEx(() -> MAPPING.mojMethod("getDataVersion"));

    default DataVersion getDataVersion() {
        return wrap(wrapReflectEx(() -> getDataVersion.invoke(__getRuntimeInstance())), DataVersion.class);
    }



    class __concrete extends ReflectWrapper implements WorldVersion {
        private __concrete(Object obj) {
            super(obj);
        }
    }
}
