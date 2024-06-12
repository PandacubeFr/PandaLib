package fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import java.nio.file.Path;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class NbtIo extends ReflectWrapper {
	public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.nbt.NbtIo"));
	private static final ReflectMethod<?> readCompressed = wrapEx(() -> REFLECT.method("readCompressed", Path.class, NbtAccounter.REFLECT.get()));
	private static final ReflectMethod<?> writeCompressed = wrapEx(() -> REFLECT.method("writeCompressed", CompoundTag.REFLECT.get(), Path.class));
	
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
