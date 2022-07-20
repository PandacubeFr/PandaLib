package fr.pandacube.lib.paper.reflect.wrapper.minecraft.network.protocol;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class ClientboundGameEventPacket extends ReflectWrapper implements Packet {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.network.protocol.game.ClientboundGameEventPacket"));
    private static final Reflect.ReflectConstructor<?> CONSTRUCTOR = wrapEx(() -> MAPPING.runtimeReflect().constructor(Type.MAPPING.runtimeClass(), float.class));
    private static final Reflect.ReflectField<?> FIELD_RAIN_LEVEL_CHANGE = wrapEx(() -> MAPPING.mojField("RAIN_LEVEL_CHANGE"));
    private static final Reflect.ReflectField<?> FIELD_THUNDER_LEVEL_CHANGE = wrapEx(() -> MAPPING.mojField("THUNDER_LEVEL_CHANGE"));

    public static Type RAIN_LEVEL_CHANGE() {
        return wrap(wrapReflectEx(FIELD_RAIN_LEVEL_CHANGE::getStaticValue), Type.class);
    }
    public static Type THUNDER_LEVEL_CHANGE() {
        return wrap(wrapReflectEx(FIELD_THUNDER_LEVEL_CHANGE::getStaticValue), Type.class);
    }

    public ClientboundGameEventPacket(Type type, float value) {
        this(wrapReflectEx(() -> CONSTRUCTOR.instanciate(unwrap(type), value)));
    }

    protected ClientboundGameEventPacket(Object obj) {
        super(obj);
    }


    public static class Type extends ReflectWrapper {
        public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.network.protocol.game.ClientboundGameEventPacket$Type"));

        protected Type(Object obj) {
            super(obj);
        }
    }
}
