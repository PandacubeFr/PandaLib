package fr.pandacube.lib.paper.reflect.wrapper.craftbukkit;

import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.OBCReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapperTyped;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ServerLevel;
import org.bukkit.World;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

public class CraftWorld extends ReflectWrapperTyped<World> {
    public static final Reflect.ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("CraftWorld"));
    public static final Reflect.ReflectMethod<?> getHandle = wrapEx(() -> REFLECT.method("getHandle"));

    public ServerLevel getHandle() {
        return wrap(wrapReflectEx(() -> getHandle.invoke(__getRuntimeInstance())), ServerLevel.class);
    }

    protected CraftWorld(Object obj) {
        super(obj);
    }
}
