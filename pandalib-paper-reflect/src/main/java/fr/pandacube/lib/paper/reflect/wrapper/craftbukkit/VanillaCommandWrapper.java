package fr.pandacube.lib.paper.reflect.wrapper.craftbukkit;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.tree.CommandNode;

import fr.pandacube.lib.paper.reflect.OBCReflect;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;
import fr.pandacube.lib.paper.reflect.wrapper.minecraft.commands.Commands;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectConstructor;
import fr.pandacube.lib.reflect.ReflectField;
import fr.pandacube.lib.reflect.ReflectMethod;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class VanillaCommandWrapper extends ReflectWrapperTyped<BukkitCommand> {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> OBCReflect.ofClass("command.VanillaCommandWrapper"));
    public static final ReflectConstructor<?> CONSTRUTOR = wrapEx(() -> REFLECT.constructor(Commands.MAPPING.runtimeClass(), CommandNode.class));
    public static final ReflectField<?> vanillaCommand = wrapEx(() -> REFLECT.field("vanillaCommand"));
    public static final ReflectMethod<?> getListener = wrapEx(() -> REFLECT.method("getListener", CommandSender.class));

    public VanillaCommandWrapper(Commands dispatcher, CommandNode<BukkitBrigadierCommandSource> vanillaCommand) {
        this(wrapReflectEx(() -> CONSTRUTOR.instanciate(unwrap(dispatcher), vanillaCommand)));
    }

    @SuppressWarnings("unchecked")
    public CommandNode<BukkitBrigadierCommandSource> vanillaCommand() {
        return (CommandNode<BukkitBrigadierCommandSource>) wrapReflectEx(() -> vanillaCommand.getValue(__getRuntimeInstance()));
    }

    public static BukkitBrigadierCommandSource getListener(CommandSender sender) {
        return (BukkitBrigadierCommandSource) wrapReflectEx(() -> getListener.invokeStatic(sender));
    }

    protected VanillaCommandWrapper(Object obj) {
        super(obj);
    }
}
