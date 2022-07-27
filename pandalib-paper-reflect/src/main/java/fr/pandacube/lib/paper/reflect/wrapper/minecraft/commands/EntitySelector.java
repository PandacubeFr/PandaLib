package fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectListWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.ServerPlayer;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.Entity;
import fr.pandacube.lib.reflect.ReflectMethod;

import java.util.List;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class EntitySelector extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.selector.EntitySelector"));
    private static final ReflectMethod<?> findEntities = wrapEx(() -> MAPPING.mojMethod("findEntities", CommandSourceStack.MAPPING));
    private static final ReflectMethod<?> findPlayers = wrapEx(() -> MAPPING.mojMethod("findPlayers", CommandSourceStack.MAPPING));
    private static final ReflectMethod<?> findSingleEntity = wrapEx(() -> MAPPING.mojMethod("findSingleEntity", CommandSourceStack.MAPPING));
    private static final ReflectMethod<?> findSinglePlayer = wrapEx(() -> MAPPING.mojMethod("findSinglePlayer", CommandSourceStack.MAPPING));

    @SuppressWarnings("unchecked")
    public ReflectListWrapper<Entity> findEntities(BukkitBrigadierCommandSource source) {
        return wrapList((List<Object>) wrapReflectEx(() -> findEntities.invoke(__getRuntimeInstance(), source)), Entity.class);
    }

    public Entity findSingleEntity(BukkitBrigadierCommandSource source) {
        return wrap(wrapReflectEx(() -> findSingleEntity.invoke(__getRuntimeInstance(), source)), Entity.class);
    }

    @SuppressWarnings("unchecked")
    public ReflectListWrapper<ServerPlayer> findPlayers(BukkitBrigadierCommandSource source) {
        return wrapList((List<Object>) wrapReflectEx(() -> findPlayers.invoke(__getRuntimeInstance(), source)), ServerPlayer.class);
    }

    public ServerPlayer findSinglePlayer(BukkitBrigadierCommandSource source) {
        return wrap(wrapReflectEx(() -> findSinglePlayer.invoke(__getRuntimeInstance(), source)), ServerPlayer.class);
    }


    protected EntitySelector(Object obj) {
        super(obj);
    }
}
