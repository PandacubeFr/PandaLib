package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.paper.configuration.WorldConfiguration;
import fr.pandacube.lib.reflect.ReflectMethod;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class Level extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.level.Level"));
    public static final ReflectMethod<?> getGameTime = wrapEx(() -> MAPPING.mojMethod("getGameTime"));
    public static final ReflectMethod<?> getFreeMapId = wrapEx(() -> MAPPING.mojMethod("getFreeMapId"));
    public static final ReflectMethod<?> paperConfig = wrapEx(() -> MAPPING.runtimeReflect().method("paperConfig")); // paper method

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
