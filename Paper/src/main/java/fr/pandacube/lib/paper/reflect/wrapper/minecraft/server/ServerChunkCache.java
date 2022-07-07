package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

public class ServerChunkCache extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.level.ServerChunkCache"));
    private static final Reflect.ReflectField<?> FIELD_chunkMap = wrapEx(() -> MAPPING.mojField("chunkMap"));

    public final ChunkMap chunkMap;

    protected ServerChunkCache(Object obj) {
        super(obj);

        chunkMap = wrap(wrapReflectEx(() -> FIELD_chunkMap.getValue(obj)));
    }
}
