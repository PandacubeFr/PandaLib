package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

public class DamageSource extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.world.damagesource.DamageSource"));
    private static final Reflect.ReflectField<?> FIELD_OUT_OF_WORLD = wrapEx(() -> MAPPING.mojField("OUT_OF_WORLD"));

    public static final DamageSource OUT_OF_WORLD = wrap(wrapReflectEx(FIELD_OUT_OF_WORLD::getStaticValue), DamageSource.class);

    protected DamageSource(Object obj) {
        super(obj);
    }
}
