package fr.pandacube.lib.paper.reflect.wrapper.craftbukkit;

import fr.pandacube.lib.paper.reflect.OBCReflect;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.Vec3;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.util.ThrowableUtil;
import org.bukkit.util.Vector;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class CraftVector extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("util.CraftVector"));
    public static final ReflectMethod<?> toBukkit_Vec3 = ThrowableUtil.wrapEx(() -> REFLECT.method("toBukkit", Vec3.REFLECT.get()));

    public static Vector toBukkit(Vec3 nms) {
        return (Vector) wrapReflectEx(() -> toBukkit_Vec3.invokeStatic(unwrap(nms)));
    }


    protected CraftVector(Object obj) {
        super(obj);
    }
}
