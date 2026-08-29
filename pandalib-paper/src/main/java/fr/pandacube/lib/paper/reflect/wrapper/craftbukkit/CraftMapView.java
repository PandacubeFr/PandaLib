package fr.pandacube.lib.paper.reflect.wrapper.craftbukkit;

import fr.pandacube.lib.paper.reflect.OBCReflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectField;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;
import org.bukkit.map.MapView;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class CraftMapView extends ReflectWrapperTyped<MapView> {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("map.CraftMapView"));
    public static final ReflectField<?> worldMap = wrapEx(() -> REFLECT.field("worldMap")); // directly used without wrapping of value

    protected CraftMapView(Object obj) {
        super(obj);
    }
}
