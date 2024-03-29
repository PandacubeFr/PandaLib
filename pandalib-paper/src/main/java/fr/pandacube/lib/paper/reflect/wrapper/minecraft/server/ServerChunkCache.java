package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.ReflectField;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ServerChunkCache extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.level.ServerChunkCache"));
    private static final ReflectField<?> FIELD_chunkMap = wrapEx(() -> MAPPING.mojField("chunkMap"));

    public final ChunkMap chunkMap;

    protected ServerChunkCache(Object obj) {
        super(obj);

        chunkMap = wrap(wrapReflectEx(() -> FIELD_chunkMap.getValue(obj)), ChunkMap.class);
    }
}
