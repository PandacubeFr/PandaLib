package fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.reflect.ReflectField;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class Commands extends ReflectWrapper {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.Commands"));
    public static final ReflectField<?> dispatcher = wrapEx(() -> MAPPING.mojField("dispatcher"));

    @SuppressWarnings("unchecked")
    public CommandDispatcher<CommandSourceStack> dispatcher() {
        return (CommandDispatcher<CommandSourceStack>) wrapReflectEx(() -> dispatcher.getValue(__getRuntimeInstance()));
    }

    protected Commands(Object obj) {
        super(obj);
    }
}
