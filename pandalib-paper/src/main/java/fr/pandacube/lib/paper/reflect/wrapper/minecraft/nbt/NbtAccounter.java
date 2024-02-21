package fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.NMSReflect.ClassMapping;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import java.io.File;
import java.nio.file.Path;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class NbtAccounter extends ReflectWrapper {
	public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.nbt.NbtAccounter"));
	private static final ReflectMethod<?> unlimitedHeap = wrapEx(() -> MAPPING.mojMethod("unlimitedHeap"));

	private NbtAccounter(Object obj) {
		super(obj);
	}


	public static NbtAccounter unlimitedHeap() {
		return wrap(wrapEx(() -> unlimitedHeap.invokeStatic()), NbtAccounter.class);
	}
}
