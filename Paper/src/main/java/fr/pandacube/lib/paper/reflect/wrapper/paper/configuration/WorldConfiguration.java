package fr.pandacube.lib.paper.reflect.wrapper.paper.configuration;

import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;

public class WorldConfiguration extends ReflectWrapper {
    public static final Reflect.ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.papermc.paper.configuration.WorldConfiguration"));
    public static final Reflect.ReflectField<?> chunks = wrapEx(() -> REFLECT.field("chunks"));

    public Chunks chunks() {
        return wrap(wrapReflectEx(() -> chunks.getValue(__getRuntimeInstance())), Chunks.class);
    }

    protected WorldConfiguration(Object obj) {
        super(obj);
    }

    public static class Chunks extends ReflectWrapper {
        public static final Reflect.ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.papermc.paper.configuration.WorldConfiguration$Chunks"));
        public static final Reflect.ReflectField<?> autoSavePeriod = wrapEx(() -> REFLECT.field("autoSaveInterval"));

        public FallbackValue_Int autoSavePeriod() {
            return wrap(wrapReflectEx(() -> autoSavePeriod.getValue(__getRuntimeInstance())), FallbackValue_Int.class);
        }

        protected Chunks(Object obj) {
            super(obj);
        }
    }
}
