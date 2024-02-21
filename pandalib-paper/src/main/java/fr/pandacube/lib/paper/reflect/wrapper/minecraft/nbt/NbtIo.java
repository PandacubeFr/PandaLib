package fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.NMSReflect.ClassMapping;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import java.nio.file.Path;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class NbtIo extends ReflectWrapper {
	public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.nbt.NbtIo"));
	private static final ReflectMethod<?> readCompressed = wrapEx(() -> MAPPING.mojMethod("readCompressed", Path.class, NbtAccounter.MAPPING));
	private static final ReflectMethod<?> writeCompressed = wrapEx(() -> MAPPING.mojMethod("writeCompressed", CompoundTag.MAPPING, Path.class));
	
	private NbtIo(Object obj) {
		super(obj);
	}


	
	public static CompoundTag readCompressed(Path p, NbtAccounter accounter) {
		return wrap(wrapEx(() -> readCompressed.invokeStatic(p, unwrap(accounter))), CompoundTag.class);
	}

	public static void writeCompressed(CompoundTag tag, Path p) {
        wrapEx(() -> writeCompressed.invokeStatic(unwrap(tag), p));
	}
}
