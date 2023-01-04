package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CompoundTag;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.ReflectMethod;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class Entity extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.entity.Entity"));
    public static final ReflectMethod<?> getBukkitEntity = wrapEx(() -> MAPPING.runtimeReflect().method("getBukkitEntity")); // spigot method
    public static final ReflectMethod<?> serializeEntity = wrapEx(() -> MAPPING.runtimeReflect().method("serializeEntity", CompoundTag.MAPPING.runtimeClass())); // paper method

    public org.bukkit.entity.Entity getBukkitEntity() {
        return (org.bukkit.entity.Entity) wrapReflectEx(() -> getBukkitEntity.invoke(__getRuntimeInstance()));
    }

    public boolean serializeEntity(CompoundTag nbt) {
        return wrapReflectEx(() -> (Boolean) serializeEntity.invoke(__getRuntimeInstance(), unwrap(nbt)));
    }

    protected Entity(Object obj) {
        super(obj);
    }
}
