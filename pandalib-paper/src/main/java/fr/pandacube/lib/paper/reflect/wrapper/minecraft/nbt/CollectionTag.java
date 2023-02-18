package fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.NMSReflect.ClassMapping;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;

import java.util.AbstractList;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class CollectionTag extends ReflectWrapperTyped<AbstractList<?>> implements Tag {
	public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.nbt.CollectionTag"));


	public int size() {
		return __getRuntimeInstance().size();
	}

	public Tag get(int i) {
		return wrap(__getRuntimeInstance().get(i), Tag.class);
	}

	public Tag set(int i, Tag t) {
		return wrap(((AbstractList<Object>)__getRuntimeInstance()).set(i, unwrap(t)), Tag.class);
	}

	public void add(int i, Tag t) {
		((AbstractList<Object>)__getRuntimeInstance()).add(i, unwrap(t));
	}

	public Tag remove(int i) {
		return wrap(__getRuntimeInstance().remove(i), Tag.class);
	}

	protected CollectionTag(Object nms) {
		super(nms);
	}
	
}
