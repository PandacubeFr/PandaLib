package fr.pandacube.lib.paper.reflect.wrapper.dataconverter;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class MCDataType extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("ca.spottedleaf.dataconverter.minecraft.datatypes.MCDataType"));

    protected MCDataType(Object obj) {
        super(obj);
    }
}
