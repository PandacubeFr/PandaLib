package fr.pandacube.lib.paper.reflect.wrapper.minecraft;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ConcreteWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperI;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

@ConcreteWrapper(GameVersion.__concrete.class)
public interface GameVersion extends ReflectWrapperI {
    ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("com.mojang.bridge.game.GameVersion"));
    ReflectMethod<?> getWorldVersion = wrapEx(() -> REFLECT.method("getWorldVersion"));

    default int getWorldVersion() {
        return (int) wrapReflectEx(() -> getWorldVersion.invoke(__getRuntimeInstance()));
    }


    class __concrete extends ReflectWrapper implements GameVersion {
        private __concrete(Object obj) {
            super(obj);
        }
    }
}
