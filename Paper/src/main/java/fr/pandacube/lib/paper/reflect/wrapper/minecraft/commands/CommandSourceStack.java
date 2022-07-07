package fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import fr.pandacube.lib.paper.reflect.NMSReflect;
import fr.pandacube.lib.paper.reflect.wrapper.ReflectWrapperTyped;

import static fr.pandacube.lib.core.util.ThrowableUtil.wrapEx;

public class CommandSourceStack extends ReflectWrapperTyped<BukkitBrigadierCommandSource> {
    public static final NMSReflect.ClassMapping MAPPING = wrapEx(() -> NMSReflect.mojClass("net.minecraft.commands.CommandSourceStack"));

    protected CommandSourceStack(Object obj) {
        super(obj);
    }
}
