package fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import fr.pandacube.lib.core.util.Reflect;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ConcreteWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapperI;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.core.BlockPos;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.world.Vec3;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.core.util.ThrowableUtil.wrapReflectEx;
import static fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapper.wrap;

@ConcreteWrapper(Coordinates.__concrete.class)
public interface Coordinates extends ReflectWrapperI {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.arguments.coordinates.Coordinates"));
    public static final Reflect.ReflectMethod<?> getPosition = wrapEx(() -> MAPPING.mojMethod("getPosition", CommandSourceStack.MAPPING));
    public static final Reflect.ReflectMethod<?> getBlockPos = wrapEx(() -> MAPPING.mojMethod("getBlockPos", CommandSourceStack.MAPPING));

    public default Vec3 getPosition(BukkitBrigadierCommandSource source) {
        return wrap(wrapReflectEx(() -> getPosition.invoke(__getRuntimeInstance(), source)), Vec3.class);
    }

    public default BlockPos getBlockPos(BukkitBrigadierCommandSource source) {
        return wrap(wrapReflectEx(() -> getBlockPos.invoke(__getRuntimeInstance(), source)), BlockPos.class);
    }

    static class __concrete extends ReflectWrapper implements Coordinates {
        protected __concrete(Object obj) {
            super(obj);
        }
    }
}
