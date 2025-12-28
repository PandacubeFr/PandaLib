package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CompoundTag;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.server.NameAndId;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import java.util.Optional;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class PlayerDataStorage extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.level.storage.PlayerDataStorage"));
    public static final ReflectMethod<?> load = wrapEx(() -> REFLECT.method("load", NameAndId.REFLECT.get()));

    /**
     * @param nameAndId the name and id of the player.
     */
    @SuppressWarnings("unchecked")
    public Optional<CompoundTag> load(NameAndId nameAndId) {
        return wrapOptional((Optional<Object>) wrapReflectEx(() -> load.invoke(__getRuntimeInstance(), unwrap(nameAndId))), CompoundTag.class);
    }



    protected PlayerDataStorage(Object obj) {
        super(obj);
    }
}
