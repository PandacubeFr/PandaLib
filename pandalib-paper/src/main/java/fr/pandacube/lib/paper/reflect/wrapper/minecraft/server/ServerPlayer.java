package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.Entity;
import fr.pandacube.lib.reflect.ReflectField;
import fr.pandacube.lib.reflect.ReflectMethod;
import org.bukkit.entity.Player;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ServerPlayer extends Entity { // in NMS, ServerPlayer is not a direct subclass of Entity
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.level.ServerPlayer"));
    public static final ReflectField<?> connection = wrapEx(() -> MAPPING.mojField("connection"));
    public static final ReflectMethod<?> isTextFilteringEnabled = wrapEx(() -> MAPPING.mojMethod("isTextFilteringEnabled"));
    public static final ReflectMethod<?> allowsListing = wrapEx(() -> MAPPING.mojMethod("allowsListing"));

    public boolean isTextFilteringEnabled() {
        return (boolean) wrapReflectEx(() -> isTextFilteringEnabled.invoke(__getRuntimeInstance()));
    }

    public boolean allowsListing() {
        return (boolean) wrapReflectEx(() -> allowsListing.invoke(__getRuntimeInstance()));
    }

    public ServerGamePacketListenerImpl connection() {
        return wrap(wrapReflectEx(() -> connection.getValue(__getRuntimeInstance())), ServerGamePacketListenerImpl.class);
    }

    @Override
    public Player getBukkitEntity() {
        return (Player) super.getBukkitEntity();
    }

    protected ServerPlayer(Object obj) {
        super(obj);
    }
}
