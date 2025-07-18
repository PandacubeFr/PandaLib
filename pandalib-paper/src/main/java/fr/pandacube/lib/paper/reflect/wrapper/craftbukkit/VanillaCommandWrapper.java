package fr.pandacube.lib.paper.reflect.wrapper.craftbukkit;

import com.mojang.brigadier.tree.CommandNode;
import fr.pandacube.lib.paper.reflect.OBCReflect;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.Commands;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectConstructor;
import fr.pandacube.lib.reflect.ReflectField;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class VanillaCommandWrapper extends ReflectWrapperTyped<BukkitCommand> {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("command.VanillaCommandWrapper"));
    public static final ReflectField<?> vanillaCommand = wrapEx(() -> REFLECT.field("vanillaCommand"));
    public static final ReflectMethod<?> getListener = wrapEx(() -> REFLECT.method("getListener", CommandSender.class));


    @SuppressWarnings("unchecked")
    public CommandNode<CommandSourceStack> vanillaCommand() {
        return (CommandNode<CommandSourceStack>) wrapReflectEx(() -> vanillaCommand.getValue(__getRuntimeInstance()));
    }

    public static CommandSourceStack getListener(CommandSender sender) {
        return (CommandSourceStack) wrapReflectEx(() -> getListener.invokeStatic(sender));
    }

    protected VanillaCommandWrapper(Object obj) {
        super(obj);
    }
}
