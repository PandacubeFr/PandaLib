package fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class NbtAccounter extends ReflectWrapper {
	public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.nbt.NbtAccounter"));
	private static final ReflectMethod<?> unlimitedHeap = wrapEx(() -> REFLECT.method("unlimitedHeap"));

	private NbtAccounter(Object obj) {
		super(obj);
	}


	public static NbtAccounter unlimitedHeap() {
		return wrap(wrapEx(() -> unlimitedHeap.invokeStatic()), NbtAccounter.class);
	}
}
