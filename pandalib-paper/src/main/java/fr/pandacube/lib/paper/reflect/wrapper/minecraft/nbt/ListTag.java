package fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.NMSReflect.ClassMapping;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ListTag extends CollectionTag {
	public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.nbt.ListTag"));
	private static final ReflectMethod<?> getCompound = wrapEx(() -> MAPPING.mojMethod("getCompound", int.class));

	public CompoundTag getCompound(int index) {
		return wrap(wrapReflectEx(() -> getCompound.invoke(__getRuntimeInstance(), index)), CompoundTag.class);
	}

	protected ListTag(Object nms) {
		super(nms);
	}
	
}
