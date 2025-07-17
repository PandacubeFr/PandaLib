package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import com.mojang.serialization.Codec;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectConstructor;
import fr.pandacube.lib.reflect.ReflectField;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ItemStackWithSlot extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.ItemStackWithSlot"));
    private static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> REFLECT.constructor(int.class, ItemStack.REFLECT.get()));
    private static final ReflectField<?> CODEC = wrapEx(() -> REFLECT.field("CODEC"));
    private static final ReflectMethod<?> slot = wrapEx(() -> REFLECT.method("slot"));
    private static final ReflectMethod<?> stack = wrapEx(() -> REFLECT.method("stack"));

    public static Codec<?> CODEC() {
        return (Codec<?>) wrapReflectEx(CODEC::getStaticValue);
    }



    protected ItemStackWithSlot(Object obj) {
        super(obj);
    }

    public ItemStackWithSlot(int slot, ItemStack stack) {
        super(wrapReflectEx(() -> CONSTRUCTOR.instantiate(slot, unwrap(stack))));
    }


    public int slot() {
        return (int) wrapReflectEx(() -> slot.invoke(__getRuntimeInstance()));
    }

    public ItemStack stack() {
        return wrap(wrapReflectEx(() -> stack.invoke(__getRuntimeInstance())), ItemStack.class);
    }

}
