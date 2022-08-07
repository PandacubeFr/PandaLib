package fr.pandacube.lib.reflect.wrapper;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectConstructor;
import fr.pandacube.lib.util.Log;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Class in which each wrapper classes must be registered, using {@link #initWrapper(Class, Class)}.
 */
public class WrapperRegistry {

    /* package */ static Class<? extends ReflectWrapperI> getWrapperOfRuntimeClass(Class<?> runtime) {
        RegistryEntry e = WRAPPER_DATA_BY_RUNTIME_CLASS.get(runtime);
        return e == null ? null : e.wrapperClass;
    }

    /* package */ static Class<?> getRuntimeClassOfWrapperClass(Class<? extends ReflectWrapperI> wrapperClass) {
        RegistryEntry e = WRAPPER_DATA_BY_WRAPPER_CLASS.get(wrapperClass);
        return e == null ? null : e.runtimeClass;
    }

    /* package */ static ReflectConstructor<? extends ReflectWrapperI> getWrapperConstructorOfWrapperClass(Class<? extends ReflectWrapperI> wrapperClass) {
        RegistryEntry e = WRAPPER_DATA_BY_WRAPPER_CLASS.get(wrapperClass);
        return e == null ? null : e.objectWrapperConstructor;
    }




    private static final Map<Class<?>, RegistryEntry> WRAPPER_DATA_BY_RUNTIME_CLASS = new HashMap<>();
    private static final Map<Class<? extends ReflectWrapperI>, RegistryEntry> WRAPPER_DATA_BY_WRAPPER_CLASS = new HashMap<>();


    /**
     * Registers a new wrapper class.
     * @param wrapper the wrapper class.
     * @param runtime the runtime class, that will be accessed reflectively by the wrapper class.
     */
    public static void initWrapper(Class<? extends ReflectWrapperI> wrapper, Class<?> runtime) {
        Class<? extends ReflectWrapperI> concreteWrapper = wrapper;
        ReflectConstructor<? extends ReflectWrapperI> objectWrapperConstructor;
        if (wrapper.isInterface() || Modifier.isAbstract(wrapper.getModifiers())) {
            ConcreteWrapper concreteWrapperAnnotation = wrapper.getAnnotation(ConcreteWrapper.class);
            if (concreteWrapperAnnotation == null || concreteWrapperAnnotation.value() == null) {
                Log.severe("The provided non-concrete (interface or abstract class) wrapper " + wrapper + " does not" +
                        " provide any concrete wrapper.");
                return;
            }
            concreteWrapper = concreteWrapperAnnotation.value();
            if (!wrapper.isAssignableFrom(concreteWrapper)) {
                Log.severe("The concrete wrapper " + concreteWrapper + " does not extends or implements " + wrapper + ".");
                return;
            }
        }
        try {
            objectWrapperConstructor = Reflect.ofClass(concreteWrapper).constructor(Object.class);
        } catch (NoSuchMethodException e) {
            Log.severe("The wrapper " + concreteWrapper + " does not provide a constructor that takes a unique" +
                    " Object parameter.", e);
            return;
        }
        RegistryEntry e = new RegistryEntry(runtime, wrapper, concreteWrapper, objectWrapperConstructor);
        WRAPPER_DATA_BY_RUNTIME_CLASS.put(runtime, e);
        WRAPPER_DATA_BY_WRAPPER_CLASS.put(wrapper, e);
        if (concreteWrapper != wrapper) {
            WRAPPER_DATA_BY_WRAPPER_CLASS.put(concreteWrapper, e);
        }
    }


    private record RegistryEntry(Class<?> runtimeClass,
                                 Class<? extends ReflectWrapperI> wrapperClass,
                                 Class<? extends ReflectWrapperI> concreteWrapperClass,
                                 ReflectConstructor<? extends ReflectWrapperI> objectWrapperConstructor) {
    }

}
