package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class SavedData extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.level.saveddata.SavedData"));
    private static final ReflectMethod<?> setDirty = wrapEx(() -> REFLECT.method("setDirty"));

    protected SavedData(Object obj) {
        super(obj);
    }

    public void setDirty() {
        wrapReflectEx(() -> setDirty.invoke(__getRuntimeInstance()));
    }
}
