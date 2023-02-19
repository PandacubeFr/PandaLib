package fr.pandacube.lib.paper.reflect.wrapper.dataconverter;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectField;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class MCTypeRegistry extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("ca.spottedleaf.dataconverter.minecraft.datatypes.MCTypeRegistry"));
    private static final ReflectField<?> _PLAYER = wrapEx(() -> REFLECT.field("PLAYER"));

    public static MCDataType PLAYER() {
        return wrap(wrapReflectEx(_PLAYER::getStaticValue), MCDataType.class);
    }



    protected MCTypeRegistry(Object obj) {
        super(obj);
    }
}
