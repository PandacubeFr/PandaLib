package fr.pandacube.lib.paper.reflect.wrapper.dataconverter;

import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CompoundTag;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class MCDataConverter extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("ca.spottedleaf.dataconverter.minecraft.MCDataConverter"));
    private static final ReflectMethod<?> convertTag = wrapEx(() -> REFLECT.method("convertTag", MCDataType.REFLECT.get(), CompoundTag.REFLECT.get(), int.class, int.class));

    public static CompoundTag convertTag(MCDataType type, CompoundTag data, int fromVersion, int toVersion) {
        return wrap(wrapReflectEx(() -> convertTag.invokeStatic(unwrap(type), unwrap(data), fromVersion, toVersion)), CompoundTag.class);
    }

    protected MCDataConverter(Object obj) {
        super(obj);
    }
}
