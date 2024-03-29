package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.ReflectMethod;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class SavedData extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.level.saveddata.SavedData"));
    private static final ReflectMethod<?> setDirty = wrapEx(() -> MAPPING.mojMethod("setDirty"));

    protected SavedData(Object obj) {
        super(obj);
    }

    public void setDirty() {
        wrapReflectEx(() -> setDirty.invoke(__getRuntimeInstance()));
    }
}
