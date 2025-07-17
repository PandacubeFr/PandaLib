package fr.pandacube.lib.paper.reflect.wrapper.minecraft.util;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectField;
import fr.pandacube.lib.reflect.wrapper.ConcreteWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperI;

import static fr.pandacube.lib.reflect.wrapper.ReflectWrapper.wrap;
import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

@ConcreteWrapper(ProblemReporter.__concrete.class)
public interface ProblemReporter extends ReflectWrapperI {
    ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.util.ProblemReporter"));
    ReflectField<?> DISCARDING = wrapEx(() -> REFLECT.field("DISCARDING"));

    static ProblemReporter DISCARDING() {
        return wrap(wrapReflectEx(DISCARDING::getStaticValue), ProblemReporter.class);
    }




    class __concrete extends ReflectWrapper implements ProblemReporter {
        protected __concrete(Object obj) {
            super(obj);
        }
    }
}
