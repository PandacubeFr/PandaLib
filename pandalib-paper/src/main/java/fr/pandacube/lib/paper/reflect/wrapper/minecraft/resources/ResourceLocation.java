package fr.pandacube.lib.paper.reflect.wrapper.minecraft.resources;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class ResourceLocation extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.resources.ResourceLocation"));


    protected ResourceLocation(Object obj) {
        super(obj);
    }
}
