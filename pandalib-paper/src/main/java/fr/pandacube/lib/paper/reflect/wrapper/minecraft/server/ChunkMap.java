package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.ChunkStorage;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectField;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ChunkMap extends ChunkStorage {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.server.level.ChunkMap"));
    private static final ReflectField<?> FIELD_level = wrapEx(() -> REFLECT.field("level"));

    public final ServerLevel level;

    protected ChunkMap(Object obj) {
        super(obj);

        level = wrap(wrapReflectEx(() -> FIELD_level.getValue(obj)), ServerLevel.class);
    }
}
