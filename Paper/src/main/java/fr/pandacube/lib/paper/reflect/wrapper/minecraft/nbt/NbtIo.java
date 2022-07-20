package fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

import java.io.File;

import fr.pandacube.lib.reflect.Reflect.ReflectMethod;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.NMSReflect.ClassMapping;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;

public class NbtIo extends ReflectWrapper {
	public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.nbt.NbtIo"));
	private static final ReflectMethod<?> readCompressed = wrapEx(() -> MAPPING.mojMethod("readCompressed", File.class));
	private static final ReflectMethod<?> writeCompressed = wrapEx(() -> MAPPING.mojMethod("writeCompressed", CompoundTag.MAPPING, File.class));
	
	private NbtIo(Object obj) {
		super(obj);
	}


	
	public static CompoundTag readCompressed(File f) {
		return new CompoundTag(wrapEx(() -> readCompressed.invokeStatic(f)));
	}
	public static void writeCompressed(CompoundTag tag, File f) {
		Object nmsTag = ReflectWrapper.unwrap(tag);
		wrapEx(() -> writeCompressed.invokeStatic(nmsTag, f));
	}
}
