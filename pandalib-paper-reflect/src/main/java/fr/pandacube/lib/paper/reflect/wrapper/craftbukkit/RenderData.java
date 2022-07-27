package fr.pandacube.lib.paper.reflect.wrapper.craftbukkit;

import fr.pandacube.lib.paper.reflect.OBCReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectField;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class RenderData extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("map.RenderData"));
    private static final ReflectField<?> buffer = wrapEx(() -> REFLECT.field("buffer"));

    protected RenderData(Object obj) {
        super(obj);
    }

    public byte[] buffer() {
        return (byte[]) wrapReflectEx(() -> buffer.getValue(__getRuntimeInstance()));
    }

    public void buffer(byte[] buff) {
        wrapReflectEx(() -> buffer.setValue(__getRuntimeInstance(), buff));
    }
}
