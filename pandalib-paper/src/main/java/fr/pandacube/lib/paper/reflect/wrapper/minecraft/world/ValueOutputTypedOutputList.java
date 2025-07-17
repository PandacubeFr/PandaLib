package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperI;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public interface ValueOutputTypedOutputList extends ReflectWrapperI {
    ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.level.storage.ValueOutput$TypedOutputList"));
    ReflectMethod<?> add = wrapEx(() -> REFLECT.method("add", Object.class));

    default void add(Object rawElement) {
        wrapReflectEx(() -> add.invoke(__getRuntimeInstance(), rawElement));
    }


    class __concrete extends ReflectWrapper implements ValueOutputTypedOutputList {
        private __concrete(Object obj) {
            super(obj);
        }
    }
}
