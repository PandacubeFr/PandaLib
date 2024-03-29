package fr.pandacube.lib.paper.reflect.wrapper.paper.configuration;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectField;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class WorldConfiguration extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.papermc.paper.configuration.WorldConfiguration"));
    public static final ReflectField<?> chunks = wrapEx(() -> REFLECT.field("chunks"));

    public Chunks chunks() {
        return wrap(wrapReflectEx(() -> chunks.getValue(__getRuntimeInstance())), Chunks.class);
    }

    protected WorldConfiguration(Object obj) {
        super(obj);
    }

    public static class Chunks extends ReflectWrapper {
        public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.papermc.paper.configuration.WorldConfiguration$Chunks"));
        public static final ReflectField<?> autoSavePeriod = wrapEx(() -> REFLECT.field("autoSaveInterval"));

        public FallbackValue_Int autoSavePeriod() {
            return wrap(wrapReflectEx(() -> autoSavePeriod.getValue(__getRuntimeInstance())), FallbackValue_Int.class);
        }

        protected Chunks(Object obj) {
            super(obj);
        }
    }
}
