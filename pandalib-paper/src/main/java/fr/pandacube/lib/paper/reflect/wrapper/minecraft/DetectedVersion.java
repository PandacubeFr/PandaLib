package fr.pandacube.lib.paper.reflect.wrapper.minecraft;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class DetectedVersion extends ReflectWrapper implements WorldVersion {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.DetectedVersion"));

    protected DetectedVersion(Object obj) {
        super(obj);
    }
}
