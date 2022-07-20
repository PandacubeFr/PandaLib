package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.util.ProgressListener;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.Level;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ServerLevel extends Level {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.level.ServerLevel"));
    public static final Reflect.ReflectMethod<?> save = wrapEx(() -> MAPPING.mojMethod("save", ProgressListener.MAPPING, boolean.class, boolean.class));
    public static final Reflect.ReflectMethod<?> getChunkSource = wrapEx(() -> MAPPING.mojMethod("getChunkSource"));


    public ServerChunkCache getChunkSource() {
        return wrap(wrapReflectEx(() -> getChunkSource.invoke(__getRuntimeInstance())), ServerChunkCache.class);
    }

    public void save(ProgressListener listener, boolean flush, boolean savingDisabled) {
        wrapReflectEx(() -> save.invoke(__getRuntimeInstance(), unwrap(listener), flush, savingDisabled));
    }


    protected ServerLevel(Object obj) {
        super(obj);
    }
}
