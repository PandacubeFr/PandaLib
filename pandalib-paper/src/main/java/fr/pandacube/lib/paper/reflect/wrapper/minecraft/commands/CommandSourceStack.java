package fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands;

import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class CommandSourceStack extends ReflectWrapperTyped<io.papermc.paper.command.brigadier.CommandSourceStack> {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("net.minecraft.commands.CommandSourceStack"));

    protected CommandSourceStack(Object obj) {
        super(obj);
    }
}
