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

public interface ValueOutput extends ReflectWrapperI {
    ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.level.storage.ValueOutput"));
    ReflectMethod<?> list = wrapEx(() -> REFLECT.method("list", String.class, Codec.class));



    default ValueOutputTypedOutputList list(String key, Codec<?> elementCodec) {
        return wrap(wrapReflectEx(() -> list.invoke(__getRuntimeInstance(), key, elementCodec)), ValueOutputTypedOutputList.class);
    }

    class __concrete extends ReflectWrapper implements ValueOutput {
        private __concrete(Object obj) {
            super(obj);
        }
    }
}
