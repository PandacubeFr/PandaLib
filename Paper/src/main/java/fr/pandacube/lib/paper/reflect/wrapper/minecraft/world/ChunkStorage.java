package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CompoundTag;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

public class ChunkStorage extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.level.chunk.storage.ChunkStorage"));
    private static final Reflect.ReflectMethod<?> read = wrapEx(() -> MAPPING.mojMethod("read", ChunkPos.MAPPING));
    private static final Reflect.ReflectMethod<?> readSync = wrapEx(() -> MAPPING.runtimeReflect().method("readSync", ChunkPos.MAPPING.runtimeReflect().get())); // spigot/paper method

    public CompoundTag readSync(ChunkPos pos) {
        return wrap(wrapReflectEx(() -> readSync.invoke(__getRuntimeInstance(), unwrap(pos))), CompoundTag.class);
    }

    public CompletableFuture<Optional<CompoundTag>> read(ChunkPos pos) {
        @SuppressWarnings("unchecked")
        CompletableFuture<Optional<?>> nmsFuture = (CompletableFuture<Optional<?>>) wrapReflectEx(() -> readSync.invoke(__getRuntimeInstance(), unwrap(pos)));
        return nmsFuture.thenApply(o -> o.map(c -> wrap(c, CompoundTag.class)));
    }

    protected ChunkStorage(Object obj) {
        super(obj);
    }
}
