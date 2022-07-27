package fr.pandacube.lib.paper.reflect.wrapper.craftbukkit;

import fr.pandacube.lib.paper.reflect.OBCReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.resources.ResourceLocation;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;

import org.bukkit.NamespacedKey;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class CraftNamespacedKey extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("util.CraftNamespacedKey"));
    public static final ReflectMethod<?> toMinecraft = wrapEx(() -> REFLECT.method("toMinecraft", NamespacedKey.class));
    public static final ReflectMethod<?> fromMinecraft = wrapEx(() -> REFLECT.method("fromMinecraft", ResourceLocation.MAPPING.runtimeClass()));

    public static ResourceLocation toMinecraft(NamespacedKey key) {
        return wrap(wrapReflectEx(() -> toMinecraft.invokeStatic(key)), ResourceLocation.class);
    }

    public static NamespacedKey fromMinecraft(ResourceLocation key) {
        return (NamespacedKey) wrapReflectEx(() -> toMinecraft.invokeStatic(unwrap(key)));
    }

    protected CraftNamespacedKey(Object obj) {
        super(obj);
    }
}
