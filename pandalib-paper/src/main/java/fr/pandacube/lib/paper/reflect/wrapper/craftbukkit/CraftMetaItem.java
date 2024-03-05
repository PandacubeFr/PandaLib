package fr.pandacube.lib.paper.reflect.wrapper.craftbukkit;

import fr.pandacube.lib.paper.reflect.OBCReflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectField;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;
import org.bukkit.inventory.meta.ItemMeta;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class CraftMetaItem extends ReflectWrapperTyped<ItemMeta> {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("inventory.CraftMetaItem"));
    public static final ReflectField<?> displayName = wrapEx(() -> REFLECT.field("displayName"));

    public String getRawDisplayName() {
        return (String) wrapReflectEx(() -> displayName.getValue(__getRuntimeInstance()));
    }

    public void setRawDisplayName(String rawDisplayName) {
        wrapReflectEx(() -> displayName.setValue(__getRuntimeInstance(), rawDisplayName));
    }


    protected CraftMetaItem(Object obj) {
        super(obj);
    }
}
