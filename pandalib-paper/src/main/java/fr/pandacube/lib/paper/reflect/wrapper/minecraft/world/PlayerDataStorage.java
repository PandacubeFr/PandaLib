package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CompoundTag;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import java.util.Optional;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class PlayerDataStorage extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.level.storage.PlayerDataStorage"));
    public static final ReflectMethod<?> load = wrapEx(() -> REFLECT.method("load", String.class, String.class));

    /**
     * @param playerName the name of the player: used for loading error message and of offline UUID generation.
     * @param playerId UUID of a player as it is used to name the player data file (UUID.toString())
     */
    @SuppressWarnings("unchecked")
    public Optional<CompoundTag> load(String playerName, String playerId) {
        return ((Optional<Object>) wrapReflectEx(() -> load.invoke(__getRuntimeInstance(), playerId)))
                .map(o -> wrap(o, CompoundTag.class));
    }



    protected PlayerDataStorage(Object obj) {
        super(obj);
    }
}
