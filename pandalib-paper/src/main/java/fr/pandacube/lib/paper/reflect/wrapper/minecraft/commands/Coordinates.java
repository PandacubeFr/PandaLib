package fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands;

import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.Vec3;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ConcreteWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperI;

import static fr.pandacube.lib.reflect.wrapper.ReflectWrapper.wrap;
import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

@ConcreteWrapper(Coordinates.__concrete.class)
public interface Coordinates extends ReflectWrapperI {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.commands.arguments.coordinates.Coordinates"));
    ReflectMethod<?> getPosition = wrapEx(() -> REFLECT.method("getPosition", CommandSourceStack.REFLECT.get()));

    default Vec3 getPosition(io.papermc.paper.command.brigadier.CommandSourceStack source) {
        return wrap(wrapReflectEx(() -> getPosition.invoke(__getRuntimeInstance(), source)), Vec3.class);
    }

    class __concrete extends ReflectWrapper implements Coordinates {
        protected __concrete(Object obj) {
            super(obj);
        }
    }
}
