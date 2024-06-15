package fr.pandacube.lib.paper.reflect.wrapper.minecraft.core;

import fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.protocol.custom.CustomPacketPayload;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public interface RegistryAccess extends HolderLookupProvider {
    ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.core.RegistryAccess"));

    class __concrete extends ReflectWrapper implements RegistryAccess {
        protected __concrete(Object obj) {
            super(obj);
        }
    }
}
