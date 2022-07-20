package fr.pandacube.lib.paper.reflect.wrapper.craftbukkit;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.paper.reflect.OBCReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.core.BlockPos;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.Vec3;
import org.bukkit.util.Vector;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class CraftVector extends ReflectWrapper {
    public static final Reflect.ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("util.CraftVector"));
    public static final Reflect.ReflectMethod<?> toBukkit_Vec3 = wrapEx(() -> REFLECT.method("toBukkit", Vec3.MAPPING.runtimeClass()));
    public static final Reflect.ReflectMethod<?> toBukkit_BlockPos = wrapEx(() -> REFLECT.method("toBukkit", BlockPos.MAPPING.runtimeClass()));
    public static final Reflect.ReflectMethod<?> toNMS = wrapEx(() -> REFLECT.method("toNMS", Vector.class));
    public static final Reflect.ReflectMethod<?> toBlockPos = wrapEx(() -> REFLECT.method("toNMS", Vector.class));

    public static Vector toBukkit(Vec3 nms) {
        return (Vector) wrapReflectEx(() -> toBukkit_Vec3.invokeStatic(unwrap(nms)));
    }

    public static Vector toBukkit(BlockPos blockPos) {
        return (Vector) wrapReflectEx(() -> toBukkit_BlockPos.invokeStatic(unwrap(blockPos)));
    }

    public static Vec3 toNMS(Vector bukkit) {
        return wrap(wrapReflectEx(() -> toNMS.invokeStatic(bukkit)), Vec3.class);
    }

    public static BlockPos toBlockPos(Vector bukkit) {
        return wrap(wrapReflectEx(() -> toBlockPos.invokeStatic(bukkit)), BlockPos.class);
    }


    protected CraftVector(Object obj) {
        super(obj);
    }
}
