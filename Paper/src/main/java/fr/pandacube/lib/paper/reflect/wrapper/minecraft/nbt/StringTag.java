package fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.NMSReflect.ClassMapping;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;

public class StringTag extends ReflectWrapper implements Tag {
	public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.nbt.StringTag"));


	public StringTag(Object nms) {
		super(nms);
	}
	
}
