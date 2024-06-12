package fr.pandacube.lib.paper.reflect.wrapper.minecraft.resources;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class ResourceLocation extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.resources.ResourceLocation"));


    protected ResourceLocation(Object obj) {
        super(obj);
    }
}
