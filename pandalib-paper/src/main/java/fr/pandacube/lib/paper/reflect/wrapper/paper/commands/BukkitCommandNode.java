package fr.pandacube.lib.paper.reflect.wrapper.paper.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.Command;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class BukkitCommandNode extends ReflectWrapperTyped<LiteralCommandNode<CommandSourceStack>> {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.papermc.paper.command.brigadier.bukkit.BukkitCommandNode"));
    private static final ReflectMethod<?> getBukkitCommand = wrapEx(() -> REFLECT.method("getBukkitCommand"));


    public Command getBukkitCommand() {
        return (Command) wrapReflectEx(() -> getBukkitCommand.invoke(__getRuntimeInstance()));
    }



    protected BukkitCommandNode(Object obj) {
        super(obj);
    }
}
