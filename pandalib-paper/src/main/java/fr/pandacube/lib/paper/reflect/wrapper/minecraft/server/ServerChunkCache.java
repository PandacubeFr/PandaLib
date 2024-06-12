package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectField;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ServerChunkCache extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.server.level.ServerChunkCache"));
    private static final ReflectField<?> FIELD_chunkMap = wrapEx(() -> REFLECT.field("chunkMap"));

    public final ChunkMap chunkMap;

    protected ServerChunkCache(Object obj) {
        super(obj);

        chunkMap = wrap(wrapReflectEx(() -> FIELD_chunkMap.getValue(obj)), ChunkMap.class);
    }
}
