package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

public class ChunkPos extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.level.ChunkPos"));
    public static final Reflect.ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> MAPPING.runtimeReflect().constructor(int.class, int.class));

    public ChunkPos(int x, int z) {
        this(wrapReflectEx(() -> CONSTRUCTOR.instanciate(x, z)));
    }

    protected ChunkPos(Object obj) {
        super(obj);
    }
}
