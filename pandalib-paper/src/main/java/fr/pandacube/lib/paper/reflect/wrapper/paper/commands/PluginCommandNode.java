package fr.pandacube.lib.paper.reflect.wrapper.paper.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.pandacube.lib.reflect.Reflect;
import fr.pandacube.lib.reflect.ReflectClass;
import fr.pandacube.lib.reflect.ReflectConstructor;
import fr.pandacube.lib.reflect.ReflectMethod;
import fr.pandacube.lib.reflect.wrapper.ReflectWrapperTyped;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.configuration.PluginMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static fr.pandacube.lib.util.ThrowableUtil.wrapEx;
import static fr.pandacube.lib.util.ThrowableUtil.wrapReflectEx;

public class PluginCommandNode extends ReflectWrapperTyped<LiteralCommandNode<CommandSourceStack>> {
    public static final ReflectClass<?> REFLECT = wrapEx(() -> Reflect.ofClass("io.papermc.paper.command.brigadier.PluginCommandNode"));
    private static final ReflectMethod<?> getPlugin = wrapEx(() -> REFLECT.method("getPlugin"));
    private static final ReflectMethod<?> getDescription = wrapEx(() -> REFLECT.method("getDescription"));
    private static final ReflectConstructor CONSTRUCTOR = wrapEx(() -> REFLECT.constructor(String.class, PluginMeta.class, LiteralCommandNode.class, String.class));


    public PluginCommandNode(@NotNull String literal, @NotNull PluginMeta plugin, @NotNull LiteralCommandNode<CommandSourceStack> rootLiteral, @Nullable String description) {
        this(wrapReflectEx(() -> CONSTRUCTOR.instantiate(literal, plugin, rootLiteral, description)));
    }


    public Plugin getPlugin() {
        return (Plugin) wrapReflectEx(() -> getPlugin.invoke(__getRuntimeInstance()));
    }

    public String getDescription() {
        return (String) wrapReflectEx(() -> getDescription.invoke(__getRuntimeInstance()));
    }



    protected PluginCommandNode(Object obj) {
        super(obj);
    }
}
