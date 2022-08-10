package fr.pandacube.lib.reflect.wrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate a concrete wrapper class that implements the annotated interface or abstract class, in case there is no
 * proper wrapper for a provided runtime object.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConcreteWrapper {
    /**
     * The class representing a dummy implementation of the annotated wrapper interface.
     * @return the class representing a dummy implementation of the annotated wrapper interface.
     */
    Class<? extends ReflectWrapper> value();
}
