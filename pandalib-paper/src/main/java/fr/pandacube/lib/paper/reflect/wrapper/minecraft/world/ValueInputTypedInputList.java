package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.wrapper.ConcreteWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTypedI;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

@ConcreteWrapper(ValueInputTypedInputList.__concrete.class)
public interface ValueInputTypedInputList extends ReflectWrapperTypedI<Iterable<?>> {
    ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.level.storage.ValueInput$TypedInputList"));


    class __concrete extends ReflectWrapperTyped<Iterable<?>> implements ValueInputTypedInputList {
        private __concrete(Object obj) {
            super(obj);
        }
    }
}
