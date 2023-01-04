package fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.NMSReflect.ClassMapping;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;

import java.util.AbstractList;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class CollectionTag extends ReflectWrapperTyped<AbstractList<?>> implements Tag {
	public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.nbt.CollectionTag"));


	public int size() {
		return __getRuntimeInstance().size();
	}

	protected CollectionTag(Object nms) {
		super(nms);
	}
	
}
