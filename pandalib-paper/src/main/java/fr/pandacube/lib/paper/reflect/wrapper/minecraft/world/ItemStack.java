package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CompoundTag;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ItemStack extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.item.ItemStack"));
    private static final ReflectMethod<?> of = wrapEx(() -> REFLECT.method("of", CompoundTag.REFLECT.get()));
    private static final ReflectMethod<?> save = wrapEx(() -> REFLECT.method("save", CompoundTag.REFLECT.get()));

    public static ItemStack of(CompoundTag nbt) {
        return wrap(wrapReflectEx(() -> of.invokeStatic(unwrap(nbt))), ItemStack.class);
    }


    protected ItemStack(Object obj) {
        super(obj);
    }


    public CompoundTag save(CompoundTag nbt) {
        return wrap(wrapReflectEx(() -> save.invoke(__getRuntimeInstance(), unwrap(nbt))), CompoundTag.class);
    }
}
