package fr.pandacube.lib.paper.reflect.wrapper.spottedleaf.moonrise;

import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.DataVersion;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ConcreteWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperI;

import static fr.pandacube.lib.reflect.wrapper.ReflectWrapper.wrap;
import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

@ConcreteWrapper(ChunkSystemChunkStorage.__concrete.class)
public interface ChunkSystemChunkStorage extends ReflectWrapperI {
    ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("ca.spottedleaf.moonrise.patches.chunk_system.storage.ChunkSystemChunkStorage"));
    ReflectMethod<?> moonrise$getRegionStorage = wrapEx(() -> REFLECT.method("moonrise$getRegionStorage"));

    default DataVersion moonrise$getRegionStorage() {
        return wrap(wrapReflectEx(() -> moonrise$getRegionStorage.invoke(__getRuntimeInstance())), DataVersion.class);
    }



    class __concrete extends ReflectWrapper implements ChunkSystemChunkStorage {
        private __concrete(Object obj) {
            super(obj);
        }
    }
}
