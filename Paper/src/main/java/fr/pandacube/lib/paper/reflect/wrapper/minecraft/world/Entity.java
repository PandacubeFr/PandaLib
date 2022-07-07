package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

public class Entity extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.entity.Entity"));
    public static final Reflect.ReflectMethod<?> getBukkitEntity = wrapEx(() -> MAPPING.runtimeReflect().method("getBukkitEntity")); // spigot field

    public org.bukkit.entity.Entity getBukkitEntity() {
        return (org.bukkit.entity.Entity) wrapReflectEx(() -> getBukkitEntity.invoke(__getRuntimeInstance()));
    }

    protected Entity(Object obj) {
        super(obj);
    }
}
