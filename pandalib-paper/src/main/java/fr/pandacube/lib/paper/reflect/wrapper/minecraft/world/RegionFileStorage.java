package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CompoundTag;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class RegionFileStorage extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.level.chunk.storage.RegionFileStorage"));
    private static final ReflectMethod<?> read = wrapEx(() -> REFLECT.method("read", ChunkPos.REFLECT.get())); // spigot/paper method

    public CompoundTag read(ChunkPos pos) {
        return wrap(wrapReflectEx(() -> read.invoke(__getRuntimeInstance(), unwrap(pos))), CompoundTag.class);
    }

    protected RegionFileStorage(Object obj) {
        super(obj);
    }
}
