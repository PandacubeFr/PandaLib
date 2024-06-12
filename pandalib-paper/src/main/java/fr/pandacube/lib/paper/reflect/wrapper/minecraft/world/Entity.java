package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CompoundTag;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class Entity extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.entity.Entity"));
    public static final ReflectMethod<?> getBukkitEntity = wrapEx(() -> REFLECT.method("getBukkitEntity")); // spigot method
    public static final ReflectMethod<?> serializeEntity = wrapEx(() -> REFLECT.method("serializeEntity", CompoundTag.REFLECT.get())); // paper method

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
