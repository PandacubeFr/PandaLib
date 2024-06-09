package fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands;

import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;

public class CommandSourceStack extends ReflectWrapperTyped<io.papermc.paper.command.brigadier.CommandSourceStack> {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.CommandSourceStack"));

    protected CommandSourceStack(Object obj) {
        super(obj);
    }
}
