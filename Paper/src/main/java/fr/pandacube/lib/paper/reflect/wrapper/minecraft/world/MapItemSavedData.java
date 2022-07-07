package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.NMSReflect;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

public class MapItemSavedData extends SavedData {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.level.saveddata.maps.MapItemSavedData"));
    public static final Reflect.ReflectField<?> colors = wrapEx(() -> MAPPING.mojField("colors"));
    public static final Reflect.ReflectField<?> locked = wrapEx(() -> MAPPING.mojField("locked"));

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
