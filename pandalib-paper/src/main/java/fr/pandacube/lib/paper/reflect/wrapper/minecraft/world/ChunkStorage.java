package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CompoundTag;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ChunkStorage extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.level.chunk.storage.ChunkStorage"));
    private static final ReflectMethod<?> readSync = wrapEx(() -> REFLECT.method("readSync", ChunkPos.REFLECT.get())); // spigot/paper method

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
