package fr.pandacube.lib.paper.reflect.wrapper.craftbukkit;

import com.mojang.brigadier.tree.CommandNode;
import fr.pandacube.lib.paper.reflect.OBCReflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectField;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.defaults.BukkitCommand;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class VanillaCommandWrapper extends ReflectWrapperTyped<BukkitCommand> {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("command.VanillaCommandWrapper"));
    public static final ReflectField<?> vanillaCommand = wrapEx(() -> REFLECT.field("vanillaCommand"));


    @SuppressWarnings("unchecked")
    public CommandNode<CommandSourceStack> vanillaCommand() {
        return (CommandNode<CommandSourceStack>) wrapReflectEx(() -> vanillaCommand.getValue(__getRuntimeInstance()));
    }

    protected VanillaCommandWrapper(Object obj) {
        super(obj);
    }
}
