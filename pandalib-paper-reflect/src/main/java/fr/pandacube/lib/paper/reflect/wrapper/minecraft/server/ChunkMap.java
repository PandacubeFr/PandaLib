package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.ChunkStorage;
import fr.pandacube.lib.paper.reflect.wrapper.paper.QueuedChangesMapLong2Object;
import fr.pandacube.lib.reflect.ReflectField;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ChunkMap extends ChunkStorage {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.level.ChunkMap"));
    private static final ReflectField<?> FIELD_level = wrapEx(() -> MAPPING.mojField("level"));

    public final ServerLevel level;

    protected ChunkMap(Object obj) {
        super(obj);

        level = wrap(wrapReflectEx(() -> FIELD_level.getValue(obj)), ServerLevel.class);
    }
}
