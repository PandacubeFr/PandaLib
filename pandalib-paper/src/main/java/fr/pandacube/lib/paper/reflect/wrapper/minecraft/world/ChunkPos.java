package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectConstructor;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ChunkPos extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.level.ChunkPos"));
    public static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> REFLECT.constructor(int.class, int.class));

    public ChunkPos(int x, int z) {
        this(wrapReflectEx(() -> CONSTRUCTOR.instantiate(x, z)));
    }

    protected ChunkPos(Object obj) {
        super(obj);
    }
}
