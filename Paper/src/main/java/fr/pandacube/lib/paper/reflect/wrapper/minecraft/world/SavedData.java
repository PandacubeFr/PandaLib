package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

public class SavedData extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.level.saveddata.SavedData"));
    public static final Reflect.ReflectMethod<?> setDirty = wrapEx(() -> MAPPING.mojMethod("setDirty"));

    protected SavedData(Object obj) {
        super(obj);
    }

    public void setDirty(boolean dirty) {
        wrapReflectEx(() -> setDirty.invoke(__getRuntimeInstance(), dirty));
    }
}
