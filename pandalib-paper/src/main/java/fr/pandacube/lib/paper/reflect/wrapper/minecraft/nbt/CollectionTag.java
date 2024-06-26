package fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;

import java.util.AbstractList;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class CollectionTag extends ReflectWrapperTyped<AbstractList<?>> implements Tag {
	public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.nbt.CollectionTag"));


	public int size() {
		return __getRuntimeInstance().size();
	}

	public Tag get(int i) {
		return wrap(__getRuntimeInstance().get(i), Tag.class);
	}

	@SuppressWarnings("unchecked")
	public Tag set(int i, Tag t) {
		return wrap(((AbstractList<Object>)__getRuntimeInstance()).set(i, unwrap(t)), Tag.class);
	}

	@SuppressWarnings("unchecked")
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
