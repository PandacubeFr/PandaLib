package fr.pandacube.lib.paper.reflect.wrapper.minecraft;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class DetectedVersion extends ReflectWrapper implements WorldVersion {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.DetectedVersion"));

    protected DetectedVersion(Object obj) {
        super(obj);
    }
}
