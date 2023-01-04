package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CompoundTag;
import fr.pandacube.lib.paper.reflect.wrapper.paper.configuration.WorldConfiguration;
import fr.pandacube.lib.reflect.ReflectField;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class PlayerDataStorage extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.level.storage.PlayerDataStorage"));
    public static final ReflectMethod<?> getPlayerData = wrapEx(() -> MAPPING.runtimeReflect().method("getPlayerData", String.class)); // Craftbukkit method

    /**
     * @param playerId UUID of a player as it is used to name the player data file (UUID.toString())
     */
    public CompoundTag getPlayerData(String playerId) {
        return wrap(wrapReflectEx(() -> getPlayerData.invoke(__getRuntimeInstance(), playerId)), CompoundTag.class);
    }



    protected PlayerDataStorage(Object obj) {
        super(obj);
    }
}
