package fr.pandacube.lib.paper.reflect.wrapper.craftbukkit;

import fr.pandacube.lib.paper.reflect.OBCReflect;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ServerLevel;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;

import org.bukkit.World;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class CraftWorld extends ReflectWrapperTyped<World> {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("CraftWorld"));
    public static final ReflectMethod<?> getHandle = wrapEx(() -> REFLECT.method("getHandle"));

    public ServerLevel getHandle() {
        return wrap(wrapReflectEx(() -> getHandle.invoke(__getRuntimeInstance())), ServerLevel.class);
    }

    protected CraftWorld(Object obj) {
        super(obj);
    }
}
