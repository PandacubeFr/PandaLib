package fr.pandacube.lib.paper.reflect.wrapper.craftbukkit;

import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.OBCReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapperTyped;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ServerPlayer;
import org.bukkit.entity.Player;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

public class CraftPlayer extends ReflectWrapperTyped<Player> {
    public static final Reflect.ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("entity.CraftPlayer"));
    public static final Reflect.ReflectMethod<?> getHandle = wrapEx(() -> REFLECT.method("getHandle"));

    public ServerPlayer getHandle() {
        return wrap(wrapReflectEx(() -> getHandle.invoke(__getRuntimeInstance())), ServerPlayer.class);
    }

    protected CraftPlayer(Object obj) {
        super(obj);
    }
}
