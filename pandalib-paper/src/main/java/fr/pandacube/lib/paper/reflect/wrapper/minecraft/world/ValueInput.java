package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import com.mojang.serialization.Codec;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperI;

import static fr.pandacube.lib.reflect.wrapper.ReflectWrapper.wrap;
import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public interface ValueInput extends ReflectWrapperI {
    ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.level.storage.ValueInput"));
    ReflectMethod<?> listOrEmpty = wrapEx(() -> REFLECT.method("listOrEmpty", String.class, Codec.class));



    default ValueInputTypedInputList listOrEmpty(String key, Codec<?> elementCodec) {
        return wrap(wrapReflectEx(() -> listOrEmpty.invoke(__getRuntimeInstance(), key, elementCodec)), ValueInputTypedInputList.class);
    }

    class __concrete extends ReflectWrapper implements ValueInput {
        private __concrete(Object obj) {
            super(obj);
        }
    }
}
