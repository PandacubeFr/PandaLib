package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectField;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class MapItemSavedData extends SavedData {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.level.saveddata.maps.MapItemSavedData"));
    public static final ReflectField<?> colors = wrapEx(() -> REFLECT.field("colors"));
    public static final ReflectField<?> locked = wrapEx(() -> REFLECT.field("locked"));

    protected MapItemSavedData(Object obj) {
        super(obj);
    }

    public boolean locked() {
        return (boolean) wrapReflectEx(() -> locked.getValue(__getRuntimeInstance()));
    }

    public void locked(boolean l) {
        wrapReflectEx(() -> locked.setValue(__getRuntimeInstance(), l));
    }

    public byte[] colors() {
        return (byte[]) wrapReflectEx(() -> colors.getValue(__getRuntimeInstance()));
    }

    public void colors(byte[] c) {
        wrapReflectEx(() -> colors.setValue(__getRuntimeInstance(), c));
    }
}
