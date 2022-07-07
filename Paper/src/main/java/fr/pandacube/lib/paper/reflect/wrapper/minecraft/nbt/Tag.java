package fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

import fr.pandacube.lib.core.util.Reflect.ReflectMethod;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.NMSReflect.ClassMapping;
import fr.pandacube.lib.paper.reflect.wrapper.ConcreteWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapperI;

@ConcreteWrapper(Tag.__concrete.class)
public interface Tag extends ReflectWrapperI {
	public static final ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.nbt.Tag"));
	public static final ReflectMethod<?> getAsString = wrapEx(() -> MAPPING.mojMethod("getAsString"));

	
	public default String getAsString() {
		return wrapReflectEx(() -> (String) getAsString.invoke(__getRuntimeInstance()));
	}
	

	
	public static class __concrete extends ReflectWrapper implements Tag {
		private __concrete(Object obj) {
			super(obj);
		}
	}

}
