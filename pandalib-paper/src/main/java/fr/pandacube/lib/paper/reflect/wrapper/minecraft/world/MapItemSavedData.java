package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.reflect.ReflectField;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class MapItemSavedData extends SavedData {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.level.saveddata.maps.MapItemSavedData"));
    public static final ReflectField<?> colors = wrapEx(() -> MAPPING.mojField("colors"));
    public static final ReflectField<?> locked = wrapEx(() -> MAPPING.mojField("locked"));

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
