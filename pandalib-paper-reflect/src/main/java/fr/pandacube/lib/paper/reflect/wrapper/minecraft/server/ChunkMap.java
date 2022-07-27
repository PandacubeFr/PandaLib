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
    private static final ReflectField<?> FIELD_autoSaveQueue = wrapEx(() -> MAPPING.runtimeReflect().field("autoSaveQueue")); // spigot/paper field
    public static final ReflectField<?> FIELD_level = wrapEx(() -> MAPPING.mojField("level"));
    public static final ReflectField<?> FIELD_pendingUnloads = wrapEx(() -> MAPPING.mojField("pendingUnloads"));
    public static final ReflectField<?> FIELD_toDrop = wrapEx(() -> MAPPING.mojField("toDrop"));
    public static final ReflectField<?> FIELD_updatingChunks = wrapEx(() -> MAPPING.runtimeReflect().field("updatingChunks")); // spigot/paper field

    /** This field in unmapped */
    public final ObjectRBTreeSet<?> autoSaveQueue;
    public final ServerLevel level;
    /** This field in unmapped */
    public final Long2ObjectLinkedOpenHashMap<?> pendingUnloads;
    /** This field in unmapped */
    public final LongSet toDrop;
    public final QueuedChangesMapLong2Object updatingChunks;

    protected ChunkMap(Object obj) {
        super(obj);

        autoSaveQueue = (ObjectRBTreeSet<?>) wrapReflectEx(() -> FIELD_autoSaveQueue.getValue(obj));
        level = wrap(wrapReflectEx(() -> FIELD_level.getValue(obj)), ServerLevel.class);
        pendingUnloads = (Long2ObjectLinkedOpenHashMap<?>) wrapReflectEx(() -> FIELD_pendingUnloads.getValue(obj));
        toDrop = (LongSet) wrapReflectEx(() -> FIELD_toDrop.getValue(obj));
        updatingChunks = wrap(wrapReflectEx(() -> FIELD_updatingChunks.getValue(obj)), QueuedChangesMapLong2Object.class);
    }
}
