package fr.pandacube.lib.paper.reflect.wrapper.minecraft.core;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.wrapper.ConcreteWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperI;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

@ConcreteWrapper(HolderLookupProvider.__concrete.class)
public interface HolderLookupProvider extends ReflectWrapperI {
    ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.core.HolderLookup.Provider"));

    class __concrete extends ReflectWrapper implements HolderLookupProvider {
        protected __concrete(Object obj) {
            super(obj);
        }
    }
}
