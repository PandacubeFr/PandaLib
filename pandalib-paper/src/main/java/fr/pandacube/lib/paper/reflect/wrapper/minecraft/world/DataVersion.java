package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class DataVersion extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.level.storage.DataVersion"));
    private static final ReflectMethod<?> getVersion = wrapEx(() -> REFLECT.method("getVersion"));
    private static final ReflectMethod<?> getSeries = wrapEx(() -> REFLECT.method("getSeries"));



    public int getVersion() {
        return (int) wrapReflectEx(() -> getVersion.invoke(__getRuntimeInstance()));
    }

    public String getSeries() {
        return (String) wrapReflectEx(() -> getSeries.invoke(__getRuntimeInstance()));
    }




    protected DataVersion(Object obj) {
        super(obj);
    }
}
