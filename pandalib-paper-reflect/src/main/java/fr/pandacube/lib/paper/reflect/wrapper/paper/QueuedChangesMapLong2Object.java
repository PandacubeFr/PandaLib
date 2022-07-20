package fr.pandacube.lib.paper.reflect.wrapper.paper;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class QueuedChangesMapLong2Object extends ReflectWrapper {
    public static final Reflect.ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("com.destroystokyo.paper.util.map.QueuedChangesMapLong2Object"));
    public static final Reflect.ReflectMethod<?> getVisibleMap = wrapEx(() -> REFLECT.method("getVisibleMap"));

    /** The entries in the returned value are not mapped */
    public Long2ObjectLinkedOpenHashMap<?> getVisibleMap() {
        return (Long2ObjectLinkedOpenHashMap<?>) wrapReflectEx(() -> getVisibleMap.invoke(__getRuntimeInstance()));
    }

    protected QueuedChangesMapLong2Object(Object obj) {
        super(obj);
    }
}
