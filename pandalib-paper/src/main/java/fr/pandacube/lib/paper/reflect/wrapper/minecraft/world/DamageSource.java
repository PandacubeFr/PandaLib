package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.ReflectField;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class DamageSource extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.damagesource.DamageSource"));
    private static final ReflectField<?> FIELD_OUT_OF_WORLD = wrapEx(() -> MAPPING.mojField("OUT_OF_WORLD"));

    public static DamageSource OUT_OF_WORLD() {
        return wrap(wrapReflectEx(FIELD_OUT_OF_WORLD::getStaticValue), DamageSource.class);
    }

    protected DamageSource(Object obj) {
        super(obj);
    }
}
