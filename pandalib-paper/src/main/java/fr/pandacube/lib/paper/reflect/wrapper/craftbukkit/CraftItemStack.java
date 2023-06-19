package fr.pandacube.lib.paper.reflect.wrapper.craftbukkit;

import fr.pandacube.lib.paper.reflect.OBCReflect;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CompoundTag;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;
import org.bukkit.inventory.ItemStack;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class CraftItemStack extends ReflectWrapperTyped<ItemStack> {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("inventory.CraftItemStack"));
    public static final ReflectMethod<?> asCraftMirror = wrapEx(() -> REFLECT.method("asCraftMirror", fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.ItemStack.MAPPING.runtimeClass()));
    public static final ReflectMethod<?> asNMSCopy = wrapEx(() -> REFLECT.method("asNMSCopy", ItemStack.class));

    public static ItemStack asCraftMirror(fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.ItemStack original) {
        return (ItemStack) wrapReflectEx(() -> asCraftMirror.invokeStatic(unwrap(original)));
    }


    public static ItemStack asCraftMirror(CompoundTag nbt) {
        return asCraftMirror(fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.ItemStack.of(nbt));
    }


    public static fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.ItemStack asNMSCopy(ItemStack original) {
        return wrap(wrapReflectEx(() -> asNMSCopy.invokeStatic(original)), fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.ItemStack.class);
    }




    protected CraftItemStack(Object obj) {
        super(obj);
    }
}
