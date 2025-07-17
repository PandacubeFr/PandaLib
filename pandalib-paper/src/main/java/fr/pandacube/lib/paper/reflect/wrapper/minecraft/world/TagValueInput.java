package fr.pandacube.lib.paper.reflect.wrapper.minecraft.world;

import fr.pandacube.lib.paper.reflect.wrapper.minecraft.nbt.CompoundTag;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.util.ProblemReporter;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class TagValueInput extends ReflectWrapper implements ValueInput {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.world.level.storage.TagValueInput"));
    private static final ReflectMethod<?> createGlobal = wrapEx(() -> REFLECT.method("createGlobal", ProblemReporter.REFLECT.get(), CompoundTag.REFLECT.get()));

    public static ValueInput createGlobal(ProblemReporter problemReporter, CompoundTag nbt) {
        return wrap(wrapReflectEx(() -> createGlobal.invokeStatic(unwrap(problemReporter), unwrap(nbt))), ValueInput.class);
    }

    protected TagValueInput(Object obj) {
        super(obj);
    }

}
