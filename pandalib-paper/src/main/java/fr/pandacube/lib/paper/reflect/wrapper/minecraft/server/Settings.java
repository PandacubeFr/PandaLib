package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.ReflectField;

import java.util.Properties;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class Settings extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.server.dedicated.Settings"));
    public static final ReflectField<?> properties = wrapEx(() -> MAPPING.mojField("properties"));

    public Properties properties() {
        return (Properties) wrapReflectEx(() -> properties.getValue(__getRuntimeInstance()));
    }

    protected Settings(Object obj) {
        super(obj);
    }
}
