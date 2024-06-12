package fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectField;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ConcreteWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperI;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

@ConcreteWrapper(Tag.__concrete.class)
public interface Tag extends ReflectWrapperI {
	ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.nbt.Tag"));
	ReflectMethod<?> getAsString = wrapEx(() -> REFLECT.method("getAsString"));
	ReflectField<?> TAG_LIST = wrapEx(() -> REFLECT.field("TAG_LIST"));
	ReflectField<?> TAG_COMPOUND = wrapEx(() -> REFLECT.field("TAG_COMPOUND"));
	ReflectField<?> TAG_ANY_NUMERIC = wrapEx(() -> REFLECT.field("TAG_ANY_NUMERIC"));

	
	default String getAsString() {
		return wrapReflectEx(() -> (String) getAsString.invoke(__getRuntimeInstance()));
	}

	static byte TAG_LIST() {
		return wrapReflectEx(() -> (byte) TAG_LIST.getStaticValue());
	}

	static byte TAG_COMPOUND() {
		return wrapReflectEx(() -> (byte) TAG_COMPOUND.getStaticValue());
	}

	static byte TAG_ANY_NUMERIC() {
		return wrapReflectEx(() -> (byte) TAG_ANY_NUMERIC.getStaticValue());
	}

	
	class __concrete extends ReflectWrapper implements Tag {
		private __concrete(Object obj) {
			super(obj);
		}
	}

}
