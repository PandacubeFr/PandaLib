package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class Entity extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.entity.Entity"));
    public static final ReflectMethod<?> getBukkitEntity = wrapEx(() -> REFLECT.method("getBukkitEntity")); // spigot method

    public org.bukkit.entity.Entity getBukkitEntity() {
        return (org.bukkit.entity.Entity) wrapReflectEx(() -> getBukkitEntity.invoke(__getRuntimeInstance()));
    }

    protected Entity(Object obj) {
        super(obj);
    }
}
