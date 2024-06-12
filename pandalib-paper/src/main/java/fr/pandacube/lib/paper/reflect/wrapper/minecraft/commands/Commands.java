package fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectField;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapper;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class Commands extends ReflectWrapper {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.commands.Commands"));
    public static final ReflectField<?> dispatcher = wrapEx(() -> REFLECT.field("dispatcher"));

    @SuppressWarnings("unchecked")
    public CommandDispatcher<CommandSourceStack> dispatcher() {
        return (CommandDispatcher<CommandSourceStack>) wrapReflectEx(() -> dispatcher.getValue(__getRuntimeInstance()));
    }

    protected Commands(Object obj) {
        super(obj);
    }
}
