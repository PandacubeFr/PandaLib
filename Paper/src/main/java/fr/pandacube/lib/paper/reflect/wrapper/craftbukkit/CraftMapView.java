package fr.pandacube.lib.paper.reflect.wrapper.craftbukkit;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.paper.reflect.OBCReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapperTyped;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.MapItemSavedData;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class CraftMapView extends ReflectWrapperTyped<MapView> {
    public static final Reflect.ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("map.CraftMapView"));
    public static final Reflect.ReflectField<?> worldMap = wrapEx(() -> REFLECT.field("worldMap"));
    public static final Reflect.ReflectMethod<?> render = wrapEx(() -> REFLECT.method("render", CraftPlayer.REFLECT.get()));

    protected CraftMapView(Object obj) {
        super(obj);
    }

    public RenderData render(Player player) {
        return wrap(wrapReflectEx(() -> render.invoke(__getRuntimeInstance(), player)), RenderData.class);
    }

    public RenderData render(CraftPlayer player) {
        return render(unwrap(player));
    }

    public MapItemSavedData worldMap() {
        return wrap(wrapReflectEx(() -> worldMap.getValue(__getRuntimeInstance())), MapItemSavedData.class);
    }

    public void worldMap(MapItemSavedData data) {
        wrapReflectEx(() -> worldMap.setValue(__getRuntimeInstance(), unwrap(data)));
    }
}
