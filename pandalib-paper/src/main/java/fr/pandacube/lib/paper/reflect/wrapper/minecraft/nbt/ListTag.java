package fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectConstructor;
import fr.pandacube.lib.reflect.ReflectMethod;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ListTag extends CollectionTag {
	public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.nbt.ListTag"));
	public static final ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> REFLECT.constructor());
	private static final ReflectMethod<?> getCompound = wrapEx(() -> REFLECT.method("getCompound", int.class));

	public CompoundTag getCompound(int index) {
		return wrap(wrapReflectEx(() -> getCompound.invoke(__getRuntimeInstance(), index)), CompoundTag.class);
	}

	public ListTag() {
		this(wrapReflectEx(() -> CONSTRUCTOR.instantiate()));
	}

	protected ListTag(Object nms) {
		super(nms);
	}
	
}
