package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.ReflectMethod;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class Entity extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.entity.Entity"));
    public static final ReflectMethod<?> getBukkitEntity = wrapEx(() -> MAPPING.runtimeReflect().method("getBukkitEntity")); // spigot field

    public org.bukkit.entity.Entity getBukkitEntity() {
        return (org.bukkit.entity.Entity) wrapReflectEx(() -> getBukkitEntity.invoke(__getRuntimeInstance()));
    }

    protected Entity(Object obj) {
        super(obj);
    }
}
