package fr.pandacube.lib.paper.reflect.wrapper.craftbukkit;

import fr.pandacube.lib.paper.reflect.OBCReflect;
import fr.pandacube.lib.reflect.ReflectField;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ServerPlayer;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class CraftPlayer extends ReflectWrapperTyped<Player> {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("entity.CraftPlayer"));
    private static final ReflectMethod<?> getHandle = wrapEx(() -> REFLECT.method("getHandle"));
    private static final ReflectField<?> invertedVisibilityEntities = wrapEx(() -> REFLECT.field("invertedVisibilityEntities"));

    public ServerPlayer getHandle() {
        return wrap(wrapReflectEx(() -> getHandle.invoke(__getRuntimeInstance())), ServerPlayer.class);
    }


    @SuppressWarnings("unchecked")
    public Map<UUID, Set<WeakReference<Plugin>>> getInvertedVisibilityEntities() {
        return (Map<UUID, Set<WeakReference<Plugin>>>) wrapReflectEx(() -> invertedVisibilityEntities.getValue(__getRuntimeInstance()));
    }

    protected CraftPlayer(Object obj) {
        super(obj);
    }
}
