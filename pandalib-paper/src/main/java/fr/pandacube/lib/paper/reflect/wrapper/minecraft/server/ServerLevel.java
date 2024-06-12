package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.paper.reflect.wrapper.minecraft.util.ProgressListener;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.Level;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ServerLevel extends Level {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.server.level.ServerLevel"));
    public static final ReflectMethod<?> save = wrapEx(() -> REFLECT.method("save", ProgressListener.REFLECT.get(), boolean.class, boolean.class));
    public static final ReflectMethod<?> getChunkSource = wrapEx(() -> REFLECT.method("getChunkSource"));


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
