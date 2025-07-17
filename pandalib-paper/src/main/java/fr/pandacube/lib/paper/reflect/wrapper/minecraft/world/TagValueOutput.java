package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CompoundTag;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.util.ProblemReporter;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class TagValueOutput extends ReflectWrapper implements ValueOutput {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.level.storage.TagValueOutput"));
    private static final ReflectMethod<?> createWrappingGlobal = wrapEx(() -> REFLECT.method("createWrappingGlobal", ProblemReporter.REFLECT.get(), CompoundTag.REFLECT.get()));

    public static TagValueOutput createWrappingGlobal(ProblemReporter problemReporter, CompoundTag nbt) {
        return wrap(wrapReflectEx(() -> createWrappingGlobal.invokeStatic(unwrap(problemReporter), unwrap(nbt))), TagValueOutput.class);
    }

    protected TagValueOutput(Object obj) {
        super(obj);
    }

}
