package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CompoundTag;
import fr.pandacube.lib.reflect.ReflectConstructor;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ItemStack extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.item.ItemStack"));
    private static final ReflectMethod<?> of = wrapEx(() -> MAPPING.mojMethod("of", CompoundTag.MAPPING));

    public static ItemStack of(CompoundTag nbt) {
        return wrap(wrapReflectEx(() -> of.invokeStatic(unwrap(nbt))), ItemStack.class);
    }

    protected ItemStack(Object obj) {
        super(obj);
    }
}
