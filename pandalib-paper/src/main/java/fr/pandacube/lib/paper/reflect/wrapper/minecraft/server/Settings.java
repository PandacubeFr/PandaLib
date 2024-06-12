package fr.pandacube.lib.paper.reflect.wrapper.minecraft.server;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectField;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import java.util.Properties;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class Settings extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.server.dedicated.Settings"));
    public static final ReflectField<?> properties = wrapEx(() -> REFLECT.field("properties"));

    public Properties properties() {
        return (Properties) wrapReflectEx(() -> properties.getValue(__getRuntimeInstance()));
    }

    protected Settings(Object obj) {
        super(obj);
    }
}
