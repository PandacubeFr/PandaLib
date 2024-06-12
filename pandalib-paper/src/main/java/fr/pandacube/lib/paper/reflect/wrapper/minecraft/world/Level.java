package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.paper.reflect.wrapper.paper.configuration.WorldConfiguration;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class Level extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.level.Level"));
    public static final ReflectMethod<?> getGameTime = wrapEx(() -> REFLECT.method("getGameTime"));
    public static final ReflectMethod<?> getFreeMapId = wrapEx(() -> REFLECT.method("getFreeMapId"));
    public static final ReflectMethod<?> paperConfig = wrapEx(() -> REFLECT.method("paperConfig")); // paper method

    public long getGameTime() {
        return (long) wrapReflectEx(() -> getGameTime.invoke(__getRuntimeInstance()));
    }

    public int getFreeMapId() {
        return (int) wrapReflectEx(() -> getFreeMapId.invoke(__getRuntimeInstance()));
    }

    public WorldConfiguration paperConfig() {
        return wrap(wrapReflectEx(() -> paperConfig.invoke(__getRuntimeInstance())), WorldConfiguration.class);
    }

    protected Level(Object obj) {
        super(obj);
    }
}
